package com.hoholms.onlinewalletapi.controller;

import com.hoholms.onlinewalletapi.entity.Profile;
import com.hoholms.onlinewalletapi.entity.Transaction;
import com.hoholms.onlinewalletapi.entity.TransactionsCategory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class ControllerUtils {
    public static Map<String, String> getErrors(BindingResult bindingResult) {
        Collector<FieldError, ?, Map<String, String>> collector = Collectors.toMap(
                fieldError -> fieldError.getField() + "Error",
                FieldError::getDefaultMessage,
                (fieldError1, fieldError2) -> fieldError1 + ". " + fieldError2
        );

        return bindingResult.getFieldErrors().stream().collect(collector);
    }

    public static Map<TransactionsCategory, BigDecimal> getCategoriesSumMap(Profile profile, boolean isIncome, LocalDate from, LocalDate to) {
        return profile.getTransactions().stream()
                .filter(transaction ->
                        transaction.getTransactionDate().isAfter(from.minusDays(1)) &&
                                transaction.getTransactionDate().isBefore(to.plusDays(1)) &&
                                transaction.getIsIncome() == isIncome)
                .collect(Collectors.groupingBy(Transaction::getCategory,
                        Collectors.mapping(Transaction::getAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));
    }
}
