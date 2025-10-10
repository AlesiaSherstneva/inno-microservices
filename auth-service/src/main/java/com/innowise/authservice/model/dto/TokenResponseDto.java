package com.innowise.authservice.model.dto;

import lombok.Data;

@Data
public class TokenResponseDto {
    private boolean valid;
    private Long userId;
    private String role;
}