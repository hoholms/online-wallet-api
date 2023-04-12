package com.hoholms.onlinewalletapi.service;

import com.hoholms.onlinewalletapi.entity.Currency;
import com.hoholms.onlinewalletapi.entity.Profile;
import com.hoholms.onlinewalletapi.entity.User;
import com.hoholms.onlinewalletapi.entity.dto.UpdateProfileDto;
import com.hoholms.onlinewalletapi.exception.*;
import com.hoholms.onlinewalletapi.repository.ProfileRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private static final Logger logger = LoggerFactory.getLogger(ProfileService.class);
    private final ProfileRepository profileRepository;
    private final UserService userService;
    private final MailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    @Value("${hostname}")
    private String hostname;

    public void save(Profile profile) {
        profileRepository.save(profile);
    }

    public boolean add(Profile profile) {
        if (profileRepository.existsProfileByEmail(profile.getEmail())) {
            logger.error("Profile with email: {} already exists", profile.getEmail());
            return false;
        }

        profile.setCurrency("USD");
        profile.setActivationCode(UUID.randomUUID().toString());
        sendMail(profile);
        profileRepository.save(profile);

        return true;
    }

    public boolean activateProfile(String code) {
        Profile profile = profileRepository.findByActivationCode(code);

        if (profile == null) {
            return false;
        }

        User user = profile.getUser();

        profile.setActivationCode(null);
        user.setEnabled(true);

        userService.save(user);
        profileRepository.save(profile);

        return true;
    }

    public void updateProfile(HttpServletRequest request, HttpServletResponse response, User user, UpdateProfileDto updateProfileDto) {
        Profile currentProfile = findProfileByUser(user);

        updateFirstName(currentProfile, updateProfileDto);
        updateLastName(currentProfile, updateProfileDto);
        updatePassword(user, updateProfileDto);
        updateEmail(request, response, user, currentProfile, updateProfileDto);
        updateCurrency(currentProfile, updateProfileDto);

        userService.save(user);
        profileRepository.save(currentProfile);
    }

    private void updateFirstName(Profile currentProfile, UpdateProfileDto updateProfileDto) {
        if (isFieldValueChanged(updateProfileDto.getFirstName(), currentProfile.getFirstName())) {
            currentProfile.setFirstName(updateProfileDto.getFirstName());
        }
    }

    private void updateLastName(Profile currentProfile, UpdateProfileDto updateProfileDto) {
        if (isFieldValueChanged(updateProfileDto.getLastName(), currentProfile.getLastName())) {
            currentProfile.setLastName(updateProfileDto.getLastName());
        }
    }

    private void updatePassword(User user, UpdateProfileDto updateProfileDto) {
        if (!ObjectUtils.isEmpty(updateProfileDto.getNewPassword())) {
            if (updateProfileDto.getOldPassword() == null ||
                    !BCrypt.checkpw(updateProfileDto.getOldPassword(), user.getPassword())) {
                throw new OldPasswordDontMatchException("Old password is incorrect");
            } else if (!updateProfileDto.getNewPassword().equals(updateProfileDto.getConfirmPassword())) {
                throw new PasswordsDontMatchException("Passwords don't match");
            }

            user.setPassword(passwordEncoder.encode(updateProfileDto.getNewPassword()));
        }
    }

    private void updateEmail(HttpServletRequest request, HttpServletResponse response, User user, Profile currentProfile, UpdateProfileDto updateProfileDto) {
        if (isFieldValueChanged(updateProfileDto.getEmail(), currentProfile.getEmail())) {
            if (!existsProfileByEmail(updateProfileDto.getEmail())) {
                updateProfileEmailAndSendMail(currentProfile, updateProfileDto);
                user.setEnabled(false);
                logger.info("Profile's by id {} email has been updated", currentProfile.getId());
            } else {
                logger.error("Profile's by id {} email failed to update", currentProfile.getId());
                throw new EmailAlreadyExistsException("Email already registered!");
            }

            logoutAuthenticatedUser(request, response);
        }
    }

    private void updateCurrency(Profile currentProfile, UpdateProfileDto updateProfileDto) {
        if (updateProfileDto.getCurrency() != null && !currentProfile.getCurrency().equals(updateProfileDto.getCurrency())) {
            try {
                Currency.valueOf(updateProfileDto.getCurrency().toUpperCase());
                currentProfile.setCurrency(updateProfileDto.getCurrency());
            } catch (IllegalArgumentException e) {
                throw new InvalidCurrencyException("Please provide an EXISTING currency");
            }
        }
    }

    private boolean isFieldValueChanged(String newValue, String oldValue) {
        return (newValue != null && !newValue.equals(oldValue) || oldValue != null && !oldValue.equals(newValue));
    }

    private void updateProfileEmailAndSendMail(Profile currentProfile, UpdateProfileDto updateProfileDto) {
        currentProfile.setEmail(updateProfileDto.getEmail());
        currentProfile.setActivationCode(UUID.randomUUID().toString());
        sendMail(currentProfile);
    }

    private void logoutAuthenticatedUser(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
    }


    public void calcBalance(User user) {
        profileRepository.calcBalance(findProfileByUser(user).getId());
    }

    public BigDecimal getCalcBalance(Profile profile) {
        return profileRepository.getCalcBalance(profile.getId());
    }

    public Profile findProfileByUser(User user) {
        return profileRepository.findByUser(user)
                .orElseThrow(() -> new ProfileNotFoundException(
                        String.format("Profile for user %s not found!", user.getId())
                ));
    }

    public boolean existsProfileByEmail(String email) {
        return profileRepository.existsProfileByEmail(email);
    }

    private void sendMail(Profile profile) {
        if (!ObjectUtils.isEmpty(profile.getEmail())) {
            String message = String.format(
                    """
                            Hello %s!
                            Welcome to Online Wallet!
                            Please visit this link: http://%s/activate/%s""",
                    profile.getUser().getUsername(),
                    hostname,
                    profile.getActivationCode()
            );

            mailSender.send(profile.getEmail(), "Online Wallet activation code", message);
        }
    }
}
