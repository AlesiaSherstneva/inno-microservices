package com.innowise.userservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserRequestDto {
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name should be between 2 and 50 characters")
    @Pattern(regexp = "^[A-Z][A-Za-z-]+$",
            message = "Name should start with capital letter and contain only English letters and hyphens")
    private String name;

    @NotBlank(message = "Surname is required")
    @Size(min = 2, max = 50, message = "Surname should be between 2 and 50 characters")
    @Pattern(regexp = "^[A-Z][A-Za-z-]+$",
            message = "Surname should start with capital letter and contain only English letters and hyphens")
    private String surname;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Birth date must be in the past")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
}