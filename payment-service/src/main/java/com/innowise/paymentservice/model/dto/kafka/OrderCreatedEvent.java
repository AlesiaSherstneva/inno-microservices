package com.innowise.paymentservice.model.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data transfer object representing an order creation event.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent {
    /**
     * Unique identifier of the order.
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