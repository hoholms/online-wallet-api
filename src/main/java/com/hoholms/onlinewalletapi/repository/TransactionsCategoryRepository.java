package com.hoholms.onlinewalletapi.repository;

import com.hoholms.onlinewalletapi.entity.TransactionsCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionsCategoryRepository extends JpaRepository<TransactionsCategory, Long> {

    List<TransactionsCategory> findByIsIncome(boolean isIncome);

    Optional<TransactionsCategory> findByCategory(String category);

    Optional<TransactionsCategory> findByCategoryAndIsIncome(String category, boolean isIncome);
}