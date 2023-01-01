package com.hoholms.onlinewalletapi.controller;

import com.hoholms.onlinewalletapi.entity.Authority;
import com.hoholms.onlinewalletapi.entity.User;
import com.hoholms.onlinewalletapi.entity.dto.UsernameDto;
import com.hoholms.onlinewalletapi.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @GetMapping
    public List<User> userList() {
        logger.info("Call for users list page");
        return userService.findAllUsers();
    }

    @GetMapping("{userID}")
    public String userEditForm(@PathVariable Long userID, Model model) {
        logger.info("Call for user with id: {} edit page", userID);
        model.addAttribute("user", userService.findUserById(userID));
        model.addAttribute("authorities", Authority.values());
        return "userEdit";
    }

    @PostMapping
    public String userSave(
            @RequestParam Long userID,
            @RequestParam(defaultValue = "false") Boolean enabled,
            @RequestParam Map<String, String> form,
            @Valid UsernameDto username,
            BindingResult bindingResult,
            Model model

    ) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errorsMap = ControllerUtils.getErrors(bindingResult);
            model.mergeAttributes(errorsMap);
            model.addAttribute("user", userService.findUserById(userID));
            model.addAttribute("authorities", Authority.values());
            model.addAttribute("failedUsername", username.getUsername());
            return "userEdit";
        } else {
            userService.updateUser(userID, username.getUsername(), enabled, form);
            logger.info("Saved user with id: {}", userID);
        }
        return "redirect:/users";
    }

    @GetMapping("/delete/{userID}")
    public String userDelete(@PathVariable Long userID) {
        userService.deleteUserById(userID);
        logger.info("Deleted user with id: {}", userID);
        return "redirect:/users";
    }
}
