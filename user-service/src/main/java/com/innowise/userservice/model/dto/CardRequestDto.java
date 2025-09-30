package com.innowise.userservice.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Data Transfer Object for creating new cards.
 * Contains minimal required data to associate a card with a user.
 * Card number and other fields are generated automatically.
 */
@Data
public class CardRequestDto {
    /** Unique identifier of the user who will own the card. */
    @NotNull(message = "User's id is required")
    private Long userId;
}