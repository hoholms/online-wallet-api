package com.hoholms.onlinewalletapi.controller;

import com.hoholms.onlinewalletapi.entity.Currency;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {
    private static final Logger logger = LoggerFactory.getLogger(WalletController.class);

    @GetMapping("/currencies")
    public ResponseEntity<Currency[]> getAllCurrencies() {
        logger.info("Fetching list of all available currencies");
        return new ResponseEntity<>(Currency.values(), HttpStatus.OK);
    }
}
