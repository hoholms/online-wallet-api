package com.hoholms.onlinewalletapi.controller;

import com.hoholms.onlinewalletapi.entity.dto.DateWithLabel;
import com.hoholms.onlinewalletapi.entity.User;
import com.hoholms.onlinewalletapi.service.ProfileService;
import com.hoholms.onlinewalletapi.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class StatisticsController {
    private final TransactionService transactionService;
    private final ProfileService profileService;

    @GetMapping("statistics")
    public String statistics(@AuthenticationPrincipal User user, Model model) {
        List<DateWithLabel> dates = transactionService.findTransactionsDatesWithLabels(user);
        model.addAttribute("dates", dates);

        model.addAttribute("currentProfile", profileService.findProfileByUser(user));

        return "statistics";
    }
}
