package com.hoholms.onlinewalletapi.entity.dto;

import com.hoholms.onlinewalletapi.entity.Transaction;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * A dto for the {@link Transaction} entity
 */
@Data
@Builder
public class TransactionWithIdDto implements Serializable {

    @NotNull
    private final Long id;

    @NotNull(message = "Please provide a category")
    private final String category;

    private final Boolean isIncome;

    @NotNull(message = "Please provide transaction amount")
    @DecimalMax(value = "1000000.0", message = "Amount must be less than a million")
    @DecimalMin(value = "0.0", message = "Amount must be greater than 0")
    @PositiveOrZero(message = "Amount must be positive")
    @Digits(integer = 100, fraction = 2, message = "Amount must have 2 fraction digits")
    private final BigDecimal amount;

    @Length(max = 255, message = "Message is too long")
    private final String message;

    private final String transactionDate;
}