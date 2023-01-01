package com.hoholms.onlinewalletapi.entity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class TransactionsCategoryDto {
    private final Long id;

    @NotBlank(message = "Please provide a category name")
    @Length(max = 50, message = "Category name is too long")
    private final String category;
    private final Boolean isIncome;
}
