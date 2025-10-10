package com.innowise.authservice.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TokenRequestDto {
    @NotBlank(message = "Token is required")
    private String token;
}