package com.hoholms.onlinewalletapi.controller;

import com.hoholms.onlinewalletapi.entity.Profile;
import com.hoholms.onlinewalletapi.entity.User;
import com.hoholms.onlinewalletapi.entity.dto.TransactionDtoConverter;
import com.hoholms.onlinewalletapi.service.ProfileService;
import com.hoholms.onlinewalletapi.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dashboard")
public class DashboardController {
    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);
    private final ProfileService profileService;
    private final TransactionService transactionService;
    private final TransactionDtoConverter transactionDtoConverter;

    @GetMapping("/month/income")
    private ResponseEntity<BigDecimal> getMonthIncome(@AuthenticationPrincipal User user) {
        Profile currentProfile = profileService.findProfileByUser(user);
        return new ResponseEntity<>(transactionService.findTranSumDateBetween(
                currentProfile,
                true,
                LocalDate.now().withDayOfMonth(1),
                LocalDate.now()
        ), HttpStatus.OK);
    }

    @GetMapping("/month/expense")
    private ResponseEntity<BigDecimal> getMonthExpense(@AuthenticationPrincipal User user) {
        Profile currentProfile = profileService.findProfileByUser(user);
        return new ResponseEntity<>(transactionService.findTranSumDateBetween(
                currentProfile,
                false,
                LocalDate.now().withDayOfMonth(1),
                LocalDate.now()
        ), HttpStatus.OK);
    }

    @GetMapping("/month/income/category")
    private ResponseEntity<Pair<String, BigDecimal>> getMaxIncomeCategory(@AuthenticationPrincipal User user) {
        Profile currentProfile = profileService.findProfileByUser(user);
        return new ResponseEntity<>(transactionService.findMaxCategorySumDateBetween(
                currentProfile,
                true,
                LocalDate.now().withDayOfMonth(1),
                LocalDate.now()
        ), HttpStatus.OK);
    }

    @GetMapping("/month/expense/category")
    private ResponseEntity<Pair<String, BigDecimal>> getMaxExpenseCategory(@AuthenticationPrincipal User user) {
        Profile currentProfile = profileService.findProfileByUser(user);
        return new ResponseEntity<>(transactionService.findMaxCategorySumDateBetween(
                currentProfile,
                false,
                LocalDate.now().withDayOfMonth(1),
                LocalDate.now()
        ), HttpStatus.OK);
    }
}
