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
public class RegisterController {
    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);
    private final ProfileService profileService;
    private final RegisterService registerService;

    @PostMapping("/register")
    public void addUser(
            @RequestBody @Valid RegisterDto registerDto
    ) {
        registerService.registerUser(registerDto);
    }

    @DeleteMapping("/activate/{code}")
    public ResponseEntity<String> activate(@PathVariable String code) {
        boolean isActivated = profileService.activateProfile(code);

        if (isActivated) {
            logger.info("Account is now activated");
        } else {
            logger.warn("Account was not activated");
            return new ResponseEntity<>("Activation code not found", HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>("Your account is now activated", HttpStatus.OK);
    }
}
