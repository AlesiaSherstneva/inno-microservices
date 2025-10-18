package com.innowise.orderservice.model.dto;

import com.innowise.orderservice.model.entity.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class OrderResponseDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 7858535961680780586L;

    private CustomerDto customer;
    private List<OrderItemResponseDto> items;
    private BigDecimal totalPrice;
    private OrderStatus status;
}