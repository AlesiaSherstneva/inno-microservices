package com.innowise.paymentservice.model.dto.kafka;

import com.innowise.paymentservice.model.entity.enums.PaymentStatus;
import lombok.Builder;
import lombok.Data;

/**
 * Data transfer object representing a payment processing result event.
 */
@Data
@Builder
public class PaymentProcessedEvent {
    /**
     * Unique identifier of the order associated with the payment.
     */
    private Long orderId;

    /**
     * Result of the payment processing attempt.
     */
    private PaymentStatus status;
}