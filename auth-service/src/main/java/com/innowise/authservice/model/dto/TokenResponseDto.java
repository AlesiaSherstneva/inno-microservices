package com.innowise.authservice.model.dto;

import lombok.Data;

/**
 * Data Transfer Object for token validation response.
 * Transfers token validation results and extracted user claims.
 */
@Data
public class TokenResponseDto {
    /** Indicates if token has valid signature and structure. */
    private boolean valid;

    /** User identifier extracted from token claims. */
    private Long userId;

    /** User role extracted from token claims (null for refresh tokens). */
    private String role;
}