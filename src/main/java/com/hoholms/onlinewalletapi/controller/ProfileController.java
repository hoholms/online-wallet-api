package com.hoholms.onlinewalletapi.controller;

import com.hoholms.onlinewalletapi.entity.Currency;
import com.hoholms.onlinewalletapi.entity.Profile;
import com.hoholms.onlinewalletapi.entity.User;
import com.hoholms.onlinewalletapi.entity.dto.PasswordChangeDto;
import com.hoholms.onlinewalletapi.entity.dto.ProfileDto;
import com.hoholms.onlinewalletapi.entity.dto.ProfileDtoConverter;
import com.hoholms.onlinewalletapi.exception.EmailAlreadyExistsException;
import com.hoholms.onlinewalletapi.exception.OldPasswordDontMatchException;
import com.hoholms.onlinewalletapi.exception.PasswordsDontMatchException;
import com.hoholms.onlinewalletapi.exception.RegisterException;
import com.hoholms.onlinewalletapi.service.ProfileService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ProfileController {
    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);
    private final ProfileService profileService;
    private final ProfileDtoConverter profileDtoConverter;

    @GetMapping("profile")
    public String getProfile(@AuthenticationPrincipal User user, Model model) {
        logger.info("Call for profile info page by user id {}", user.getId());

        Profile currentProfile = profileService.findProfileByUser(user);

        model.addAttribute("username", user.getUsername());
        model.addAttribute("profileDto", profileDtoConverter.toDto(currentProfile));
        model.addAttribute("currencies", Currency.values());

        return "profile";
    }

    @PostMapping("profile")
    public String updateProfile(
            HttpServletRequest request,
            @AuthenticationPrincipal User user,
            @Valid PasswordChangeDto passwordChangeDto,
            BindingResult passwordBindingResult,
            @Valid ProfileDto profileDto,
            BindingResult profileBindingResult,
            Model model
    ) {
        String response = "profile";

        if (passwordBindingResult.hasErrors() || profileBindingResult.hasErrors()) {
            Map<String, String> passwordErrorsMap = ControllerUtils.getErrors(passwordBindingResult);
            Map<String, String> profileErrorsMap = ControllerUtils.getErrors(profileBindingResult);
            model.mergeAttributes(passwordErrorsMap);
            model.mergeAttributes(profileErrorsMap);
        } else {
            try {
                response = profileService.updateProfile(request, user, profileDto, passwordChangeDto);
                logger.info("User {} profile has been updated", user.getUsername());
                model.addAttribute("message", "Profile successfully updated!");
            } catch (RegisterException e) {
                if (e instanceof OldPasswordDontMatchException) {
                    model.addAttribute("oldPasswordError", e.getMessage());
                } else if (e instanceof PasswordsDontMatchException) {
                    model.addAttribute("confirmPasswordError", e.getMessage());
                } else if (e instanceof EmailAlreadyExistsException) {
                    model.addAttribute("emailError", e.getMessage());
                }
            }
        }

        model.addAttribute("username", user.getUsername());
        model.addAttribute("profileDto", profileDto);
        model.addAttribute("currencies", Currency.values());

        return response;
    }
}
