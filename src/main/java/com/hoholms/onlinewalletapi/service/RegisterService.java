package com.hoholms.onlinewalletapi.service;

import com.hoholms.onlinewalletapi.entity.Authority;
import com.hoholms.onlinewalletapi.entity.Profile;
import com.hoholms.onlinewalletapi.entity.User;
import com.hoholms.onlinewalletapi.entity.dto.ProfileDtoConverter;
import com.hoholms.onlinewalletapi.entity.dto.RegisterDto;
import com.hoholms.onlinewalletapi.entity.dto.UserDtoConverter;
import com.hoholms.onlinewalletapi.exception.EmailAlreadyExistsException;
import com.hoholms.onlinewalletapi.exception.PasswordsDontMatchException;
import com.hoholms.onlinewalletapi.exception.UsernameAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RegisterService {
    private static final Logger logger = LoggerFactory.getLogger(RegisterService.class);
    private final UserDtoConverter userDtoConverter;
    private final ProfileDtoConverter profileDtoConverter;
    private final UserService userService;
    private final ProfileService profileService;


    public void registerUser(RegisterDto registerDto) {
        User user = User.builder()
                .id(null)
                .username(registerDto.getUsername())
                .password(registerDto.getPassword())
                .authority(Collections.singleton(Authority.USER))
                .build();
        Profile profile = Profile.builder()
                .user(user)
                .firstName(registerDto.getFirstName())
                .lastName(registerDto.getLastName())
                .email(registerDto.getEmail())
                .balance(BigDecimal.ZERO)
                .createdDate(Instant.now())
                .build();
        String passwordConfirm = registerDto.getPasswordConfirm();

        if (userService.existsUserByUsername(user.getUsername())) {
            logger.error("User not added, because user {} already exists", user.getUsername());
            throw new UsernameAlreadyExistsException("User already exists!");
        } else if (profileService.existsProfileByEmail(profile.getEmail())) {
            logger.error("User not added, because email {} exists", profile.getEmail());
            throw new EmailAlreadyExistsException("Email already registered!");
        } else if (!Objects.equals(user.getPassword(), passwordConfirm)) {
            logger.error("User not added, because passwords not matching");
            throw new PasswordsDontMatchException("Passwords don't match!");
        }

        userService.add(user);
        profileService.add(profile);
    }
}
