package com.hoholms.onlinewalletapi.controller;

import com.hoholms.onlinewalletapi.entity.Profile;
import com.hoholms.onlinewalletapi.entity.Transaction;
import com.hoholms.onlinewalletapi.entity.User;
import com.hoholms.onlinewalletapi.entity.dto.TransactionDto;
import com.hoholms.onlinewalletapi.entity.dto.TransactionDtoConverter;
import com.hoholms.onlinewalletapi.service.ProfileService;
import com.hoholms.onlinewalletapi.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dashboard")
public class DashboardController {
    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);
    private final ProfileService profileService;
    private final TransactionService transactionService;
    private final TransactionDtoConverter transactionDtoConverter;

    @PostMapping
    public ResponseEntity<Transaction> addTransaction(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid TransactionDto transactionDto
    ) {
        Profile currentProfile = profileService.findProfileByUser(user);

            Transaction transaction = transactionDtoConverter.fromDto(transactionDto, currentProfile);
            transactionService.add(transaction, currentProfile);
            currentProfile.setBalance(profileService.getCalcBalance(currentProfile));

        profileService.calcBalance(user);

        logger.info("User by id {} added new transaction", user.getId());
        return new ResponseEntity<>(transaction, HttpStatus.OK);
    }

    @GetMapping("/profile")
    private Profile getProfile(@AuthenticationPrincipal User user) {
        logger.info("Dashboard profile info call by user id {}", user.getId());
        return profileService.findProfileByUser(user);
    }

    @GetMapping("/transactions")
    private List<Transaction> getTransactions(@AuthenticationPrincipal User user) {
        logger.info("Dashboard transactions info call by user id {}", user.getId());
        Profile currentProfile = profileService.findProfileByUser(user);
        return currentProfile.getTransactions()
                .stream()
                .sorted(Comparator.comparing(Transaction::getTransactionDate)
                        .thenComparing(Transaction::getId).reversed())
                .toList();
    }

    @GetMapping("/month/income")
    private BigDecimal getMonthIncome(@AuthenticationPrincipal User user) {
        Profile currentProfile = profileService.findProfileByUser(user);
        return transactionService.findTranSumDateBetween(
                currentProfile,
                true,
                LocalDate.now().withDayOfMonth(1),
                LocalDate.now()
        );
    }

    @GetMapping("/month/expense")
    private BigDecimal getMonthExpense(@AuthenticationPrincipal User user) {
        Profile currentProfile = profileService.findProfileByUser(user);
        return transactionService.findTranSumDateBetween(
                currentProfile,
                false,
                LocalDate.now().withDayOfMonth(1),
                LocalDate.now()
        );
    }

    @GetMapping("/month/income/category")
    private Pair<String, BigDecimal> getMaxIncomeCategory(@AuthenticationPrincipal User user) {
        Profile currentProfile = profileService.findProfileByUser(user);
        return transactionService.findMaxCategorySumDateBetween(
                currentProfile,
                true,
                LocalDate.now().withDayOfMonth(1),
                LocalDate.now()
        );
    }

    @GetMapping("/month/expense/category")
    private Pair<String, BigDecimal> getMaxExpenseCategory(@AuthenticationPrincipal User user) {
        Profile currentProfile = profileService.findProfileByUser(user);
        return transactionService.findMaxCategorySumDateBetween(
                currentProfile,
                false,
                LocalDate.now().withDayOfMonth(1),
                LocalDate.now()
        );
    }
}
