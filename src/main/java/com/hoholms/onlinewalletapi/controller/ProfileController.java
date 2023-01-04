package com.hoholms.onlinewalletapi.controller;

import com.hoholms.onlinewalletapi.entity.Profile;
import com.hoholms.onlinewalletapi.entity.User;
import com.hoholms.onlinewalletapi.entity.dto.UpdateProfileDto;
import com.hoholms.onlinewalletapi.exception.RegisterException;
import com.hoholms.onlinewalletapi.service.ProfileService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {
    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);
    private final ProfileService profileService;

    @GetMapping
    private ResponseEntity<Profile> getProfile(@AuthenticationPrincipal User user) {
        logger.info("Profile info call by user id {}", user.getId());
        return new ResponseEntity<>(profileService.findProfileByUser(user), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<Map<String, String>> updateProfile(
            HttpServletRequest request,
            HttpServletResponse response,
            @AuthenticationPrincipal User user,
            @RequestBody @Valid UpdateProfileDto updateProfileDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ControllerUtils.getErrors(bindingResult));
        } else {
            try {
                profileService.updateProfile(request, response, user, updateProfileDto);
                logger.info("User {} profile has been updated", user.getUsername());
            } catch (RegisterException e) {
                return new ResponseEntity<>(new HashMap<>() {{
                    put(e.getClass().toString(), e.getMessage());
                }}, HttpStatus.BAD_REQUEST);
            }
        }

        return new ResponseEntity<>(new HashMap<>() {{
            put("message", "Profile was successfully updated!");
        }}, HttpStatus.OK);
    }
}
