package com.hoholms.onlinewalletapi.service;

import com.hoholms.onlinewalletapi.entity.*;
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
import java.util.stream.Collectors;

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
        transaction.setCategory(categoryRepository.findByCategory(transactionDto.getCategory())
                .orElseThrow(() -> new TransactionCategoryNotFoundException("Transaction category not found!")));
        transaction.setTransactionDate(parseDate(transactionDto.getTransactionDate()));
        transaction.setMessage(transaction.getMessage());

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
        Map<TransactionsCategory, BigDecimal> sumMap = profile.getTransactions().stream()
                .filter(transaction ->
                        transaction.getTransactionDate().isAfter(from.minusDays(1)) &&
                        transaction.getTransactionDate().isBefore(to.plusDays(1)) &&
                        transaction.getIsIncome() == isIncome)
                .collect(Collectors.groupingBy(Transaction::getCategory,
                        Collectors.mapping(Transaction::getAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));

        if (sumMap.size() == 0) {
            return Pair.of("nothing", BigDecimal.ZERO);
        }

        Map.Entry<TransactionsCategory, BigDecimal> maxEntry = null;
        for (Map.Entry<TransactionsCategory, BigDecimal> entry : sumMap.entrySet()) {
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) == 1) {
                maxEntry = entry;
            }
        }

        return Pair.of(maxEntry.getKey().getCategory(), maxEntry.getValue());
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

    // TODO Fix future dates
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

        transactionRepository.deleteById(transactionID);

        profileService.save(currentProfile);
    }
}
