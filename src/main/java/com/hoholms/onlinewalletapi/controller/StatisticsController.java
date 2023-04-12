package com.hoholms.onlinewalletapi.controller;

import com.hoholms.onlinewalletapi.entity.User;
import com.hoholms.onlinewalletapi.entity.dto.CircleStatistics;
import com.hoholms.onlinewalletapi.entity.dto.DateWithLabel;
import com.hoholms.onlinewalletapi.entity.dto.LineStatistics;
import com.hoholms.onlinewalletapi.service.StatisticsService;
import com.hoholms.onlinewalletapi.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/statistics")
public class StatisticsController {
    private static final Logger logger = LoggerFactory.getLogger(StatisticsController.class);
    private final StatisticsService statisticsService;
    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<List<DateWithLabel>> statistics(@AuthenticationPrincipal User user) {
        logger.info("Fetching transaction dates with labels for user id {}", user.getId());
        return new ResponseEntity<>(transactionService.findTransactionsDatesWithLabels(user), HttpStatus.OK);
    }

    @GetMapping("/line")
    public ResponseEntity<List<LineStatistics>> getLineStatistics(@AuthenticationPrincipal User user) {
        logger.info("Fetching line statistics for user id {}", user.getId());
        return new ResponseEntity<>(statisticsService.findLineStatistics(user), HttpStatus.OK);
    }

    @GetMapping("/circle")
    public ResponseEntity<List<CircleStatistics>> getCircleStatistics(@AuthenticationPrincipal User user) {
        logger.info("Fetching circle statistics for user id {}", user.getId());
        return new ResponseEntity<>(statisticsService.findCircleStatistics(user), HttpStatus.OK);
    }

    @PutMapping("/line")
    public ResponseEntity<List<LineStatistics>> getLineStatisticsByDates(
            @AuthenticationPrincipal User user,
            @RequestParam String from,
            @RequestParam String to
    ) {
        logger.info("Fetching line statistics for user id {} between {} and {}", user.getId(), from, to);
        return new ResponseEntity<>(statisticsService.findLineStatistics(user, new DateWithLabel(from), new DateWithLabel(to)),
                HttpStatus.OK);
    }

    @PutMapping("/circle")
    public ResponseEntity<List<CircleStatistics>> getCircleStatisticsByDates(
            @AuthenticationPrincipal User user,
            @RequestParam String from,
            @RequestParam String to
    ) {
        logger.info("Fetching circle statistics for user id {} between {} and {}", user.getId(), from, to);
        return new ResponseEntity<>(statisticsService.findCircleStatistics(user, new DateWithLabel(from), new DateWithLabel(to)),
                HttpStatus.OK);
    }
}