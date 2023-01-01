package com.hoholms.onlinewalletapi.controller;

import com.hoholms.onlinewalletapi.entity.Profile;
import com.hoholms.onlinewalletapi.entity.Transaction;
import com.hoholms.onlinewalletapi.entity.TransactionsCategory;
import com.hoholms.onlinewalletapi.entity.User;
import com.hoholms.onlinewalletapi.entity.dto.TransactionDto;
import com.hoholms.onlinewalletapi.entity.dto.TransactionDtoConverter;
import com.hoholms.onlinewalletapi.service.ProfileService;
import com.hoholms.onlinewalletapi.service.TransactionService;
import com.hoholms.onlinewalletapi.service.TransactionsCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class DashboardController {
    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);
    private final ProfileService profileService;
    private final TransactionService transactionService;
    private final TransactionsCategoryService categoryService;
    private final TransactionDtoConverter transactionDtoConverter;

    @GetMapping("/dashboard")
    public String dashboard(
            @AuthenticationPrincipal User user,
            Model model
    ) {
        logger.info("Call for dashboard page by user id {}", user.getId());
        Profile currentProfile = profileService.findProfileByUser(user);
        currentProfile.setBalance(profileService.getCalcBalance(currentProfile));

        setModel(model, currentProfile);

        return "dashboard";
    }

    @PostMapping("/dashboard")
    public String addTransaction(
            @AuthenticationPrincipal User user,
            @Valid TransactionDto transactionDto,
            BindingResult bindingResult,
            Model model
    ) {
        Profile currentProfile = profileService.findProfileByUser(user);

        if (bindingResult.hasErrors()) {
            Map<String, String> errorsMap = ControllerUtils.getErrors(bindingResult);
            model.addAttribute("errorsMap", errorsMap);
            model.addAttribute("transactionDto", transactionDto);
        } else {
            Transaction transaction = transactionDtoConverter.fromDto(transactionDto, currentProfile);
            transactionService.add(transaction, currentProfile);
            currentProfile.setBalance(profileService.getCalcBalance(currentProfile));
        }

        setModel(model, currentProfile);

        profileService.calcBalance(user);

        return "dashboard";
    }

    private void setModel(Model model, Profile currentProfile) {
        model.addAttribute("currentProfile", currentProfile);

        model.addAttribute("recentTransactions", currentProfile.getTransactions()
                .stream()
                .sorted(Comparator.comparing(Transaction::getTransactionDate)
                        .thenComparing(Transaction::getId).reversed())
                .toList());

        List<TransactionsCategory> incomeCategories = categoryService.findByIsIncome(true).stream()
                .sorted(Comparator.comparing(TransactionsCategory::getId))
                .toList();
        model.addAttribute("incomeCategories", incomeCategories);

        List<TransactionsCategory> expenseCategories = categoryService.findByIsIncome(false).stream()
                .sorted(Comparator.comparing(TransactionsCategory::getId))
                .toList();
        model.addAttribute("expenseCategories", expenseCategories);

        BigDecimal monthIncome = transactionService.findTranSumDateBetween(
                currentProfile,
                true,
                LocalDate.now().withDayOfMonth(1),
                LocalDate.now()
        );
        model.addAttribute("monthIncome", monthIncome);

        BigDecimal monthExpense = transactionService.findTranSumDateBetween(
                currentProfile,
                false,
                LocalDate.now().withDayOfMonth(1),
                LocalDate.now()
        );
        model.addAttribute("monthExpense", monthExpense);


        Pair<String, BigDecimal> maxIncomeCategory = transactionService.findMaxCategorySumDateBetween(
                currentProfile,
                true,
                LocalDate.now().withDayOfMonth(1),
                LocalDate.now()
        );
        model.addAttribute("maxIncomeCategory", maxIncomeCategory);

        Pair<String, BigDecimal> maxExpenseCategory = transactionService.findMaxCategorySumDateBetween(
                currentProfile,
                false,
                LocalDate.now().withDayOfMonth(1),
                LocalDate.now()
        );
        model.addAttribute("maxExpenseCategory", maxExpenseCategory);

        model.addAttribute("today", LocalDate.now());
    }
}
