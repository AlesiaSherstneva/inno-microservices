package com.innowise.orderservice.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OrderRequestDto {
    @NotNull(message = "Items list is required")
    @Size(min = 1, message = "Order must contain at least one item")
    private List<OrderItemRequestDto> items;
}