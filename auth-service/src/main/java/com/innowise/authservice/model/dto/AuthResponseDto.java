package com.innowise.authservice.model.dto;

import lombok.Data;

@Data
public class AuthResponseDto {
    private final String accessToken;
    private final String refreshToken;
}