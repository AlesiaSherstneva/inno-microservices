package com.innowise.orderservice.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderItemResponseDto {
    private String itemName;
    private Integer quantity;
}