package com.hoholms.onlinewalletapi.controller;

import com.hoholms.onlinewalletapi.entity.Profile;
import com.hoholms.onlinewalletapi.entity.User;
import com.hoholms.onlinewalletapi.entity.dto.UpdateProfileDto;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
public class ProfileController {
    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);
    private final ProfileService profileService;

    @GetMapping
    public ResponseEntity<Profile> getProfile(@AuthenticationPrincipal User user) {
        logger.info("Profile info call by user id {}", user.getId());
        return new ResponseEntity<>(profileService.findProfileByUser(user), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<Void> updateProfile(
            HttpServletRequest request,
            HttpServletResponse response,
            @AuthenticationPrincipal User user,
            @RequestBody @Valid UpdateProfileDto updateProfileDto
    ) {
        profileService.updateProfile(request, response, user, updateProfileDto);
        logger.info("User's {} profile has been updated", user.getUsername());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
