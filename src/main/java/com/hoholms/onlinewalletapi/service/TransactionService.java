package com.hoholms.onlinewalletapi.service;

import com.hoholms.onlinewalletapi.controller.ControllerUtils;
import com.hoholms.onlinewalletapi.entity.Profile;
import com.hoholms.onlinewalletapi.entity.Transaction;
import com.hoholms.onlinewalletapi.entity.TransactionsCategory;
import com.hoholms.onlinewalletapi.entity.User;
import com.hoholms.onlinewalletapi.entity.dto.CircleStatistics;
import com.hoholms.onlinewalletapi.entity.dto.DateWithLabel;
import com.hoholms.onlinewalletapi.entity.dto.TransactionDto;
import com.hoholms.onlinewalletapi.exception.TransactionCategoryNotFoundException;
import com.hoholms.onlinewalletapi.exception.TransactionNotFoundException;
import com.hoholms.onlinewalletapi.repository.TransactionRepository;
import com.hoholms.onlinewalletapi.repository.TransactionsCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);
    private final TransactionRepository transactionRepository;
    private final TransactionsCategoryRepository categoryRepository;
    private final ProfileService profileService;

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    public LocalDate parseDate(String transactionDate) {
        return LocalDate.parse(transactionDate);
    }

    public void add(Transaction transaction, Profile profile) {
        transactionRepository.save(transaction);
        profileService.save(profile);
        logger.info("Transaction with id: {} was added", transaction.getId());
    }

    public void save(User user, Long id, TransactionDto transactionDto) {
        Profile currentProfile = profileService.findProfileByUser(user);
        Transaction transaction = findTransactionByIdAndProfile(id, currentProfile);

        if (transactionDto.getAmount() != null) {
            transaction.setAmount(transactionDto.getAmount());
        }
        transaction.setCategory(categoryRepository.findByCategoryAndIsIncome(transactionDto.getCategory(), transactionDto.getIsIncome())
                .orElseThrow(() -> new TransactionCategoryNotFoundException("Transaction category not found!")));
        transaction.setTransactionDate(parseDate(transactionDto.getTransactionDate()));
        transaction.setMessage(transactionDto.getMessage());
        currentProfile.setBalance(profileService.getCalcBalance(currentProfile));
        profileService.calcBalance(user);

        transactionRepository.save(transaction);
        profileService.save(currentProfile);
    }

    public Transaction findTransactionById(Long id) {
        return transactionRepository.findTransactionById(id)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction with id: " + id + " not found"));
    }

    public Transaction findTransactionByIdAndProfile(Long id, Profile profile) {
        return transactionRepository.findTransactionByIdAndProfile(id, profile)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction with id: " + id + " not found"));
    }

    public BigDecimal findTranSumDateBetween(Profile profile, boolean isIncome, LocalDate from, LocalDate to) {
        List<Transaction> transactions = transactionRepository.findByProfileAndIsIncomeAndTransactionDateBetween(
                profile,
                isIncome,
                from,
                to);

        return transactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Pair<String, BigDecimal> findMaxCategorySumDateBetween(
            Profile profile,
            boolean isIncome,
            LocalDate from,
            LocalDate to
    ) {
        Map<TransactionsCategory, BigDecimal> sumMap = ControllerUtils.getCategoriesSumMap(profile, isIncome, from, to);

        Optional<Map.Entry<TransactionsCategory, BigDecimal>> maxEntryOptional = sumMap.entrySet().stream()
                .max(Comparator.comparing(Map.Entry::getValue));

        if (maxEntryOptional.isPresent()) {
            Map.Entry<TransactionsCategory, BigDecimal> maxEntry = maxEntryOptional.get();
            return Pair.of(maxEntry.getKey().getCategory(), maxEntry.getValue());
        } else {
            return Pair.of("Nothing", BigDecimal.ZERO);
        }
    }

    public CircleStatistics findCategoryAndSumByProfileAndIsIncome(Profile profile, Boolean isIncome) {
        return new CircleStatistics(
                transactionRepository.findCategoryByProfileAndIsIncome(profile, isIncome),
                transactionRepository.findCategorySumByProfileAndIsIncome(profile, isIncome)
        );
    }

    public CircleStatistics findCategoryAndSumByProfileAndIsIncome(Profile profile, Boolean isIncome, DateWithLabel from, DateWithLabel to) {
        return new CircleStatistics(
                transactionRepository.findCategoryByProfileAndIsIncomeDateBetween(profile, isIncome, from.getDate(), to.getDate()),
                transactionRepository.findCategorySumByProfileAndIsIncomeDateBetween(profile, isIncome, from.getDate(), to.getDate())
        );
    }

    public List<DateWithLabel> findTransactionsDatesWithLabels(User user) {
        Profile profile = profileService.findProfileByUser(user);

        List<LocalDate> dates = profile.getTransactions().stream()
                .map(transaction -> transaction.getTransactionDate().withDayOfMonth(1))
                .filter(distinctByKey(LocalDate::getMonth))
                .sorted(Comparator.naturalOrder())
                .toList();

        return dates.stream()
                .map(DateWithLabel::new)
                .toList();
    }

    public List<DateWithLabel> findTransactionsDatesWithLabels(Profile profile, DateWithLabel from, DateWithLabel to) {
        List<LocalDate> dates = profile.getTransactions().stream()
                .map(transaction -> transaction.getTransactionDate().withDayOfMonth(1))
                .filter(distinctByKey(LocalDate::getMonth))
                .filter(date -> (date.isAfter(from.getDate()) || date.isEqual(from.getDate())) &&
                        (date.isBefore(to.getDate()) || date.isEqual(to.getDate())))
                .sorted(Comparator.naturalOrder())
                .toList();

        return dates.stream()
                .map(DateWithLabel::new)
                .toList();
    }

    public void deleteTransactionById(Long transactionID, User user) {
        Profile currentProfile = profileService.findProfileByUser(user);

        Transaction transactionFromDb = findTransactionByIdAndProfile(transactionID, currentProfile);
        transactionRepository.delete(transactionFromDb);
        currentProfile.setBalance(profileService.getCalcBalance(currentProfile));
        profileService.calcBalance(user);

        profileService.save(currentProfile);
    }
}
