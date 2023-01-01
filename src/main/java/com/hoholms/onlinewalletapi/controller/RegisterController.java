package com.hoholms.onlinewalletapi.controller;

import com.hoholms.onlinewalletapi.entity.dto.ProfileDto;
import com.hoholms.onlinewalletapi.entity.dto.UserDto;
import com.hoholms.onlinewalletapi.exception.EmailAlreadyExistsException;
import com.hoholms.onlinewalletapi.exception.PasswordsDontMatchException;
import com.hoholms.onlinewalletapi.exception.RegisterException;
import com.hoholms.onlinewalletapi.exception.UsernameAlreadyExistsException;
import com.hoholms.onlinewalletapi.service.ProfileService;
import com.hoholms.onlinewalletapi.service.RegisterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class RegisterController {
    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);
    private final ProfileService profileService;
    private final RegisterService registerService;

    @GetMapping("/register")
    public String register() {
        logger.info("Call for register page");
        return "register";
    }

    @PostMapping("/register")
    public String addUser(
            @RequestParam String passwordConfirm,
            @Valid UserDto userDto,
            BindingResult userBindingResult,
            @Valid ProfileDto profileDto,
            BindingResult profileBindingResult,
            Model model
    ) {
        if (profileBindingResult.hasErrors() || userBindingResult.hasErrors()) {
            Map<String, String> profileErrorsMap = ControllerUtils.getErrors(profileBindingResult);
            Map<String, String> userErrorsMap = ControllerUtils.getErrors(userBindingResult);
            model.mergeAttributes(profileErrorsMap);
            model.mergeAttributes(userErrorsMap);
            model.addAttribute("userDto", userDto);
            model.addAttribute("profileDto", profileDto);
        } else {
            try {
                registerService.registerUser(userDto, profileDto, passwordConfirm);
            } catch (RegisterException e) {
                if (e.getClass() == UsernameAlreadyExistsException.class) {
                    model.addAttribute("usernameError", e.getMessage());
                } else if (e.getClass() == EmailAlreadyExistsException.class) {
                    model.addAttribute("emailError", e.getMessage());
                } else if (e.getClass() == PasswordsDontMatchException.class) {
                    model.addAttribute("passwordConfirmError", e.getMessage());
                }

                model.addAttribute("userDto", userDto);
                model.addAttribute("profileDto", profileDto);

                return "register";
            }

            model.addAttribute("message", "Now confirm your email!");
            return "login";
        }

        return "register";
    }

    @GetMapping("/activate/{code}")
    public String activate(Model model, @PathVariable String code) {
        boolean isActivated = profileService.activateProfile(code);
        model.addAttribute("isActivated", isActivated);

        if (isActivated) {
            model.addAttribute("message", "Your account is now activated!");
            logger.info("Account is now activated");

        } else {
            model.addAttribute("message", "Activation code is not found!");
            logger.warn("Account was not activated");
        }

        return "info";
    }
}
