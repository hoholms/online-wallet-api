package com.hoholms.onlinewalletapi.controller;

import com.hoholms.onlinewalletapi.entity.TransactionsCategory;
import com.hoholms.onlinewalletapi.service.TransactionsCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class WalletController {
    @GetMapping("/")
    public String hello() {
        return "Hello world!";
    }
}
