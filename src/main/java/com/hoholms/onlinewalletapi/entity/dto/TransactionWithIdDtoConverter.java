package com.hoholms.onlinewalletapi.entity.dto;

import com.hoholms.onlinewalletapi.entity.Profile;
import com.hoholms.onlinewalletapi.entity.Transaction;
import com.hoholms.onlinewalletapi.service.TransactionsCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class TransactionWithIdDtoConverter {
    private final TransactionsCategoryService categoryService;

    public Transaction fromDto(TransactionWithIdDto transactionDto, Profile profile) {
        return Transaction.builder()
                .profile(profile)
                .category(categoryService.findByCategory(transactionDto.getCategory()))
                .isIncome(transactionDto.getIsIncome())
                .amount(transactionDto.getAmount())
                .message(transactionDto.getMessage())
                .transactionDate(transactionDto.getTransactionDate() != null ? LocalDate.parse(transactionDto.getTransactionDate()) : LocalDate.now())
                .build();
    }

    public TransactionWithIdDto toDto(Transaction transaction) {
        return TransactionWithIdDto.builder()
                .id(transaction.getId())
                .category(transaction.getCategory().getCategory())
                .transactionDate(transaction.getTransactionDate().toString())
                .amount(transaction.getAmount())
                .isIncome(transaction.getIsIncome())
                .message(transaction.getMessage())
                .build();
    }
}
