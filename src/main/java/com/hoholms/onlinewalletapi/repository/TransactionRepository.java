package com.hoholms.onlinewalletapi.repository;

import com.hoholms.onlinewalletapi.entity.Profile;
import com.hoholms.onlinewalletapi.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findTransactionByProfileOrderByTransactionDateAsc(Profile profile);

    Optional<Transaction> findTransactionById(Long id);

    Optional<Transaction> findTransactionByIdAndProfile(Long id, Profile profile);

    List<Transaction> findByProfileAndIsIncomeAndTransactionDateBetween(Profile profile, Boolean isIncome, LocalDate from, LocalDate to);

    @Query(value = """
            SELECT category
            FROM transactions INNER JOIN transactions_categories tc on tc.id = transactions.category_id
            WHERE profile_id = :cur_profile AND transactions.is_income = :isIncome
            GROUP BY category""", nativeQuery = true)
    List<String> findCategoryByProfileAndIsIncome(@Param("cur_profile") Profile profile, @Param("isIncome") Boolean isIncome);

    @Query(value = """
            SELECT sum(amount)
            FROM transactions INNER JOIN transactions_categories tc on tc.id = transactions.category_id
            WHERE profile_id = :cur_profile AND transactions.is_income = :isIncome
            GROUP BY category""", nativeQuery = true)
    List<BigDecimal> findCategorySumByProfileAndIsIncome(@Param("cur_profile") Profile profile, @Param("isIncome") Boolean isIncome);

    @Query(value = """
            SELECT category
            FROM transactions INNER JOIN transactions_categories tc on tc.id = transactions.category_id
            WHERE profile_id = :cur_profile
            AND transactions.is_income = :isIncome
            AND transaction_date >= :d_from
            AND transaction_date <= :d_to
            GROUP BY category""", nativeQuery = true)
    List<String> findCategoryByProfileAndIsIncomeDateBetween(
            @Param("cur_profile") Profile profile,
            @Param("isIncome") Boolean isIncome,
            @Param("d_from") LocalDate from,
            @Param("d_to") LocalDate to
    );

    @Query(value = """
            SELECT sum(amount)
            FROM transactions INNER JOIN transactions_categories tc on tc.id = transactions.category_id
            WHERE profile_id = :cur_profile
            AND transactions.is_income = :isIncome
            AND transaction_date >= :d_from
            AND transaction_date <= :d_to
            GROUP BY category""", nativeQuery = true)
    List<BigDecimal> findCategorySumByProfileAndIsIncomeDateBetween(
            @Param("cur_profile") Profile profile,
            @Param("isIncome") Boolean isIncome,
            @Param("d_from") LocalDate from,
            @Param("d_to") LocalDate to
    );
}