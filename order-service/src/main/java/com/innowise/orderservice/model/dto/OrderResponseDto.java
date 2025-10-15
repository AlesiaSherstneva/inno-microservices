package com.innowise.orderservice.model.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class OrderResponseDto {
    private Long userId;
    private List<OrderItemResponseDto> items;
    private BigDecimal totalPrice;
}