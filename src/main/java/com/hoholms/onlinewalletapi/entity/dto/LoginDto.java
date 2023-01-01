package com.hoholms.onlinewalletapi.entity.dto;

import com.hoholms.onlinewalletapi.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * A dto for the {@link User} entity
 */
@Data
public class LoginDto implements Serializable {
    @NotBlank(message = "Please provide a username")
    @Length(max = 50, message = "Username is too long")
    private final String username;

    @NotBlank(message = "Please provide a password")
    @Length(min = 8, max = 500, message = "Password must be at least 8 characters")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=\\S+$).{8,}$", message = "Password must have at least 1 number and 1 letter")
    private final String password;
}