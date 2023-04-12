package com.hoholms.onlinewalletapi.controller;

import com.hoholms.onlinewalletapi.entity.TransactionsCategory;
import com.hoholms.onlinewalletapi.entity.dto.TransactionsCategoryDto;
import com.hoholms.onlinewalletapi.service.TransactionsCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class TransactionsCategoryController {
    private static final Logger logger = LoggerFactory.getLogger(TransactionsCategoryController.class);
    private final TransactionsCategoryService transactionsCategoryService;

    @GetMapping
    public ResponseEntity<List<TransactionsCategory>> getAllCategories() {
        return new ResponseEntity<>(transactionsCategoryService.findAllCategoriesOrderById(), HttpStatus.OK);
    }

    @GetMapping("/income")
    public ResponseEntity<List<TransactionsCategory>> getIncomeCategories() {
        List<TransactionsCategory> incomeCategories = transactionsCategoryService.findByIsIncome(true).stream()
                .sorted(Comparator.comparing(TransactionsCategory::getId))
                .toList();
        return new ResponseEntity<>(incomeCategories, HttpStatus.OK);
    }

    @GetMapping("/expense")
    public ResponseEntity<List<TransactionsCategory>> getExpenseCategories() {
        List<TransactionsCategory> expenseCategories = transactionsCategoryService.findByIsIncome(false).stream()
                .sorted(Comparator.comparing(TransactionsCategory::getId))
                .toList();
        return new ResponseEntity<>(expenseCategories, HttpStatus.OK);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<TransactionsCategory> getCategoryById(@PathVariable Long categoryId) {
        return new ResponseEntity<>(transactionsCategoryService.findById(categoryId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<TransactionsCategory> createCategory(
            @RequestBody @Valid TransactionsCategoryDto categoryDto
    ) {
        TransactionsCategory createdCategory = transactionsCategoryService.addCategory(categoryDto);
        logger.info("Category \"{}\" added.", categoryDto.getCategory());
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<TransactionsCategory> updateCategory(
            @PathVariable Long categoryId,
            @RequestBody @Valid TransactionsCategoryDto categoryDto
    ) {
        TransactionsCategory updatedCategory = transactionsCategoryService.updateCategory(categoryId, categoryDto);
        logger.info("Category \"{}\" updated.", categoryDto.getCategory());
        return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
    }

    @DeleteMapping("/{categoryID}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryID) {
        transactionsCategoryService.deleteCategoryById(categoryID);
        logger.info("Category with id {} deleted.", categoryID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
