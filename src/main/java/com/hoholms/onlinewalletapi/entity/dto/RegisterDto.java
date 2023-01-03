package com.hoholms.onlinewalletapi.entity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.Transient;

@Transient
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterDto {
    @NotBlank(message = "Please provide a username")
    @Length(max = 50, message = "Username is too long")
    private String username;

    @NotBlank(message = "Please provide a password")
    @Length(min = 8, max = 500, message = "Password must be at least 8 characters")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=\\S+$).{8,}$", message = "Password must have at least 1 number and 1 letter")
    private String password;

    @NotBlank(message = "Please provide a password confirmation")
    private String passwordConfirm;

    @NotBlank(message = "Please provide your first name")
    @Length(max = 50, message = "First name is too long")
    private String firstName;

    @NotBlank(message = "Please provide your last name")
    @Length(max = 50, message = "Last name is too long")
    private String lastName;

    @NotBlank(message = "Please provide an email")
    @Email(message = "Please provide a valid email")
    private String email;
}
