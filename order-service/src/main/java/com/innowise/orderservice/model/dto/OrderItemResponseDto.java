package com.innowise.orderservice.model.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
public class OrderItemResponseDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 6813888785825133851L;

    private String name;
    private BigDecimal price;
    private Integer quantity;
}