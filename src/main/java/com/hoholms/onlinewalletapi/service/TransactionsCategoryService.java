package com.hoholms.onlinewalletapi.service;

import com.hoholms.onlinewalletapi.entity.Transaction;
import com.hoholms.onlinewalletapi.entity.TransactionsCategory;
import com.hoholms.onlinewalletapi.entity.dto.TransactionsCategoryDto;
import com.hoholms.onlinewalletapi.entity.dto.TransactionsCategoryDtoConverter;
import com.hoholms.onlinewalletapi.exception.TransactionCategoryNotFoundException;
import com.hoholms.onlinewalletapi.repository.TransactionsCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionsCategoryService {

    private static final String TRANSACTION_CATEGORY_NOT_FOUND_MESSAGE = "Transaction category not found!";

    private final TransactionsCategoryRepository categoryRepository;

    private final TransactionService transactionService;

    private final TransactionsCategoryDtoConverter categoryDtoConverter;

    public List<TransactionsCategory> findAllCategoriesByTransactionIdByIsIncome(Long transactionId) {

        Transaction transaction = transactionService.findTransactionById(transactionId);

        TransactionsCategory category = transaction.getCategory();

        return categoryRepository.findAll().stream()
                .filter(a -> a.getIsIncome().equals(category.getIsIncome()))
                .toList();
    }

    public TransactionsCategory findByCategory(String category) {
        return categoryRepository.findByCategory(category)
                .orElseThrow(() -> new TransactionCategoryNotFoundException(TRANSACTION_CATEGORY_NOT_FOUND_MESSAGE));
    }

    public TransactionsCategory findByCategoryAndIsIncome(String category, boolean isIncome) {
        return categoryRepository.findByCategoryAndIsIncome(category, isIncome)
                .orElseThrow(() -> new TransactionCategoryNotFoundException(TRANSACTION_CATEGORY_NOT_FOUND_MESSAGE));
    }

    public List<TransactionsCategory> findByIsIncome(boolean isIncome) {
        return categoryRepository.findByIsIncome(isIncome);
    }

    public List<TransactionsCategory> findAllCategoriesOrderByIsIncome() {
        return categoryRepository.findAll(Sort.by(Sort.Direction.DESC, "isIncome"));
    }

    public List<TransactionsCategory> findAllCategoriesOrderById() {
        return categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    public TransactionsCategory updateCategory(Long categoryId, TransactionsCategoryDto categoryDto) {
        TransactionsCategory categoryFromDB = categoryRepository.findById(categoryId).orElseThrow(() -> new TransactionCategoryNotFoundException("Transaction categoryDto not found!"));
        categoryFromDB.setCategory(categoryDto.getCategory());
        categoryFromDB.setIsIncome(categoryDto.getIsIncome());
        return categoryFromDB;
    }

    public void deleteCategoryById(Long id) {
        categoryRepository.deleteById(id);
    }

    public TransactionsCategory findById(Long categoryID) {
        return categoryRepository.findById(categoryID).orElseThrow(() -> new TransactionCategoryNotFoundException(TRANSACTION_CATEGORY_NOT_FOUND_MESSAGE));
    }

    public void addCategory(String category, Boolean isIncome) {
        TransactionsCategory transactionsCategory = TransactionsCategory.builder()
                .category(category)
                .isIncome(isIncome)
                .build();
        categoryRepository.save(transactionsCategory);
    }

    public TransactionsCategory addCategory(TransactionsCategoryDto categoryDto) {
        TransactionsCategory transactionsCategory = categoryDtoConverter.fromDto(categoryDto);
        categoryRepository.save(transactionsCategory);

        return transactionsCategory;
    }
}
