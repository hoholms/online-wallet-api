package com.hoholms.onlinewalletapi.controller;

import com.hoholms.onlinewalletapi.entity.User;
import com.hoholms.onlinewalletapi.entity.dto.UsernameDto;
import com.hoholms.onlinewalletapi.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<User>> userList() {
        logger.info("Call for users list");
        return new ResponseEntity<>(userService.findAllUsers(), HttpStatus.OK);
    }

    @GetMapping("{userID}")
    public ResponseEntity<User> userEditForm(@PathVariable Long userID) {
        logger.info("Call for user with id: {} edit", userID);
        return new ResponseEntity<>(userService.findUserById(userID), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Void> userSave(
            @RequestParam Long userID,
            @RequestParam(defaultValue = "false") Boolean enabled,
            @RequestParam Map<String, String> form,
            @Valid UsernameDto username

    ) {
        userService.updateUser(userID, username.getUsername(), enabled, form);
        logger.info("Saved user with id: {}", userID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{userID}")
    public ResponseEntity<Void> userDelete(@PathVariable Long userID) {
        userService.deleteUserById(userID);
        logger.info("Deleted user with id: {}", userID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}