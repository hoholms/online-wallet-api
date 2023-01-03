package com.hoholms.onlinewalletapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WalletController {
    @GetMapping("/")
    public String hello() {
        return "Hello world!";
    }
}
