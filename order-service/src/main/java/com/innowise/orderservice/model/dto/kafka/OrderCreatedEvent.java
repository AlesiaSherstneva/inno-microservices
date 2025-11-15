package com.innowise.orderservice.model.dto.kafka;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Data transfer object representing an order creation event.
 */
@Data
@Builder
public class OrderCreatedEvent {
    /**
     * Unique identifier of the created order.
     */
    private Long orderId;

    /**
     * Identifier of the user who placed the order.
     */
    private Long userId;

    /**
     * Total amount to be paid for the order.
     */
    private BigDecimal paymentAmount;
}