package com.innowise.userservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CardInfoRequestDto {
    @NotNull(message = "User's id is required")
    private Long userId;
}