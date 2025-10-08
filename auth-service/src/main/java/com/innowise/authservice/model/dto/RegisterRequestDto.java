package com.innowise.authservice.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterRequestDto {
    private String name;
    private String surname;
    private LocalDate birthDate;
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+375(17|25|29|33|44)\\d{7}$",
            message = "Phone number must be in format +375XXXXXXXXX with valid operator code")
    private String phoneNumber;

    @NotBlank
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
}