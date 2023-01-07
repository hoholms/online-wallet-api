package com.hoholms.onlinewalletapi.controller;

import com.hoholms.onlinewalletapi.entity.TransactionsCategory;
import com.hoholms.onlinewalletapi.entity.dto.TransactionsCategoryDto;
import com.hoholms.onlinewalletapi.service.TransactionsCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class TransactionsCategoryController {
    private static final Logger logger = LoggerFactory.getLogger(TransactionsCategoryController.class);
    private final TransactionsCategoryService transactionsCategoryService;

    @GetMapping
    public List<TransactionsCategory> getCategories() {
        return transactionsCategoryService.findAllCategoriesOrderById();
    }

    @GetMapping("/income")
    public List<TransactionsCategory> getIncomeCategories() {
        return transactionsCategoryService.findByIsIncome(true).stream()
                .sorted(Comparator.comparing(TransactionsCategory::getId))
                .toList();
    }

    @GetMapping("/expense")
    public List<TransactionsCategory> getExpenseCategories() {
        return transactionsCategoryService.findByIsIncome(false).stream()
                .sorted(Comparator.comparing(TransactionsCategory::getId))
                .toList();
    }

    @GetMapping("{categoryId}")
    public TransactionsCategory categoryEditForm(@PathVariable Long categoryId) {
        return transactionsCategoryService.findById(categoryId);
    }

    @PostMapping("/edit")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionsCategory addCategory(
            @RequestBody @Valid TransactionsCategoryDto categoryDto
    ) {
        logger.info("Category \"{}\" added.", categoryDto.getCategory());
        return transactionsCategoryService.addCategory(categoryDto);
    }

    @PutMapping("/edit/{categoryId}")
    public TransactionsCategory updateCategory(
            @PathVariable Long categoryId,
            @RequestBody @Valid TransactionsCategoryDto categoryDto
    ) {
        logger.info("Category \"{}}\" added.", categoryDto.getCategory());

        return transactionsCategoryService.updateCategory(categoryId, categoryDto);
    }

    @DeleteMapping("/edit/{categoryID}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long categoryID) {
        transactionsCategoryService.deleteCategoryById(categoryID);
    }
}
