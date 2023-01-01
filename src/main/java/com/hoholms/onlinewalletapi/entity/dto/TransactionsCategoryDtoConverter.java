package com.hoholms.onlinewalletapi.entity.dto;

import com.hoholms.onlinewalletapi.entity.TransactionsCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionsCategoryDtoConverter {
    public TransactionsCategory fromDto(TransactionsCategoryDto categoryDto) {
        return TransactionsCategory.builder()
                .id(categoryDto.getId())
                .category(categoryDto.getCategory())
                .isIncome(categoryDto.getIsIncome())
                .build();
    }
}
