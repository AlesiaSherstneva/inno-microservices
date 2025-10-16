package com.innowise.orderservice.model.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderItemResponseDto {
    private String name;
    private BigDecimal price;
    private Integer quantity;
}