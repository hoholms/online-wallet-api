package com.hoholms.onlinewalletapi.controller;

import com.hoholms.onlinewalletapi.entity.CircleStatistics;
import com.hoholms.onlinewalletapi.entity.DateWithLabel;
import com.hoholms.onlinewalletapi.entity.LineStatistics;
import com.hoholms.onlinewalletapi.entity.User;
import com.hoholms.onlinewalletapi.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatisticsRestController {
    private final StatisticsService statisticsService;

    @GetMapping("statistics/line")
    public List<LineStatistics> getLineStatistics(@AuthenticationPrincipal User user) {
        return statisticsService.findLineStatistics(user);
    }

    @GetMapping("statistics/circle")
    public List<CircleStatistics> getCircleStatistics(@AuthenticationPrincipal User user) {
        return statisticsService.findCircleStatistics(user);
    }

    @PostMapping("statistics/line")
    public List<LineStatistics> getLineStatisticsByDates(
            @AuthenticationPrincipal User user,
            @RequestParam String from,
            @RequestParam String to
    ) {
        return statisticsService.findLineStatistics(user, new DateWithLabel(from), new DateWithLabel(to));
    }

    @PostMapping("statistics/circle")
    public List<CircleStatistics> getCircleStatisticsByDates(
            @AuthenticationPrincipal User user,
            @RequestParam String from,
            @RequestParam String to
    ) {
        return statisticsService.findCircleStatistics(user, new DateWithLabel(from), new DateWithLabel(to));
    }
}
