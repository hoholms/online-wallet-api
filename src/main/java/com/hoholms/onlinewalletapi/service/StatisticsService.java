package com.hoholms.onlinewalletapi.service;

import com.hoholms.onlinewalletapi.controller.ControllerUtils;
import com.hoholms.onlinewalletapi.entity.Profile;
import com.hoholms.onlinewalletapi.entity.Transaction;
import com.hoholms.onlinewalletapi.entity.User;
import com.hoholms.onlinewalletapi.entity.dto.CircleStatistics;
import com.hoholms.onlinewalletapi.entity.dto.DateWithLabel;
import com.hoholms.onlinewalletapi.entity.dto.LineStatistics;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsService {
    private final ProfileService profileService;
    private final TransactionService transactionService;

    private static List<CircleStatistics> getCircleStatistics(Profile currentProfile, DateWithLabel from, DateWithLabel to) {
        CircleStatistics incomeStatistics = new CircleStatistics(ControllerUtils.getCategoriesSumMap(currentProfile, true, from.getDate(), to.getDate()));
        CircleStatistics expenseStatistics = new CircleStatistics(ControllerUtils.getCategoriesSumMap(currentProfile, false, from.getDate(), to.getDate()));

        List<CircleStatistics> statistics = new ArrayList<>();
        statistics.add(incomeStatistics);
        statistics.add(expenseStatistics);

        return statistics;
    }

    public List<LineStatistics> findLineStatistics(User user) {
        Profile currentProfile = profileService.findProfileByUser(user);

        List<DateWithLabel> dates = transactionService.findTransactionsDatesWithLabels(user);

        return getLineStatistics(currentProfile, dates);
    }

    public List<LineStatistics> findLineStatistics(User user, DateWithLabel from, DateWithLabel to) {
        Profile currentProfile = profileService.findProfileByUser(user);

        if (from.getDate().isAfter(to.getDate())) {
            DateWithLabel tmp = from;
            from = to;
            to = tmp;
        }

        List<DateWithLabel> dates = transactionService.findTransactionsDatesWithLabels(currentProfile, from, to);

        return getLineStatistics(currentProfile, dates);
    }

    private List<LineStatistics> getLineStatistics(Profile currentProfile, List<DateWithLabel> dates) {
        LineStatistics incomeStatistics = new LineStatistics(new ArrayList<>(), new ArrayList<>());
        LineStatistics expenseStatistics = new LineStatistics(new ArrayList<>(), new ArrayList<>());

        for (DateWithLabel date : dates) {
            LocalDate lastDay;
            if (date.getDate().getMonthValue() == LocalDate.now().getMonthValue() &&
                    date.getDate().getYear() == LocalDate.now().getYear()) {
                lastDay = LocalDate.now();
            } else {
                lastDay = date.getDate().withDayOfMonth(date.getDate().getMonth().length(LocalDate.now().isLeapYear()));
            }
            BigDecimal incSum = transactionService.findTranSumDateBetween(
                    currentProfile,
                    true,
                    date.getDate(),
                    lastDay
            );
            incomeStatistics.getValues().add(incSum);
            incomeStatistics.getLabels().add(date.getLabel());

            BigDecimal expSum = transactionService.findTranSumDateBetween(
                    currentProfile,
                    false,
                    date.getDate(),
                    lastDay
            );
            expenseStatistics.getValues().add(expSum);
            expenseStatistics.getLabels().add(date.getLabel());
        }

        List<LineStatistics> statistics = new ArrayList<>();
        statistics.add(incomeStatistics);
        statistics.add(expenseStatistics);

        return statistics;
    }

    public List<CircleStatistics> findCircleStatistics(User user) {
        Profile currentProfile = profileService.findProfileByUser(user);

        LocalDate defaultDate = LocalDate.parse("1970-01-01");
        DateWithLabel from = new DateWithLabel(currentProfile.getTransactions()
                .stream()
                .sorted(Comparator.comparing(Transaction::getTransactionDate))
                .reduce((first, second) -> first)
                .map(Transaction::getTransactionDate)
                .orElse(defaultDate));
        DateWithLabel to = new DateWithLabel(LocalDate.now());

        return getCircleStatistics(currentProfile, from, to);
    }


    public List<CircleStatistics> findCircleStatistics(User user, DateWithLabel from, DateWithLabel to) {
        Profile currentProfile = profileService.findProfileByUser(user);

        if (from.getDate().isAfter(to.getDate())) {
            DateWithLabel tmp = from;
            from = to;
            to = tmp;
        }

        if (to.getDate().getMonthValue() == LocalDate.now().getMonthValue() &&
                to.getDate().getYear() == LocalDate.now().getYear()) {
            to.setDate(LocalDate.now());
        } else {
            to.setDate(to.getDate().withDayOfMonth(to.getDate().getMonth().length(LocalDate.now().isLeapYear())));
        }

        from.setDate(from.getDate().withDayOfMonth(1));


        return getCircleStatistics(currentProfile, from, to);
    }
}
