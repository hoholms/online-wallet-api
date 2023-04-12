package com.hoholms.onlinewalletapi.controller;

import com.hoholms.onlinewalletapi.entity.dto.RegisterDto;
import com.hoholms.onlinewalletapi.service.ProfileService;
import com.hoholms.onlinewalletapi.service.RegisterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/register")
public class RegisterController {
    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);
    private final ProfileService profileService;
    private final RegisterService registerService;

    @PostMapping
    public ResponseEntity<Void> registerUser(
            @RequestBody @Valid RegisterDto registerDto
    ) {
        registerService.registerUser(registerDto);
        logger.info("User {} registered successfully", registerDto.getUsername());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/activate/{code}")
    public ResponseEntity<String> activateAccount(@PathVariable String code) {
        boolean isActivated = profileService.activateProfile(code);

        if (isActivated) {
            logger.info("Account with activation code {} is now activated", code);
            return new ResponseEntity<>("Your account is now activated", HttpStatus.OK);
        } else {
            logger.warn("Account with activation code {} was not activated", code);
            return new ResponseEntity<>("Activation code not found", HttpStatus.UNAUTHORIZED);
        }
    }
}