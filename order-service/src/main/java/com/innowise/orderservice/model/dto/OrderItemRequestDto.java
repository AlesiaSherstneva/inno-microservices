package com.innowise.orderservice.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderItemRequestDto {
    @NotNull(message = "Item id is required")
    @Positive(message = "Item id must be positive")
    private Integer itemId;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;
}