package com.hoholms.onlinewalletapi.entity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class UsernameDto {
    @NotBlank(message = "Please provide a username")
    @Length(max = 50, message = "Username is too long")
    private final String username;
}
