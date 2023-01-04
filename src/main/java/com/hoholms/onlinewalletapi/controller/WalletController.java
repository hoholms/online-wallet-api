package com.hoholms.onlinewalletapi.controller;

import com.hoholms.onlinewalletapi.entity.Currency;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WalletController {
    @GetMapping("/")
    public String hello() {
        return "Hello world!";
    }

    @GetMapping("/currencies")
    public ResponseEntity<Currency[]> getCurrencies() {
        return new ResponseEntity<>(Currency.values(), HttpStatus.OK);
    }
}
