package com.innowise.orderservice.model.dto.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data transfer object representing a payment processing result event.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentProcessedEvent {
    /**
     * Unique identifier of the order associated with the payment.
     */
    private Long orderId;

    /**
     * Result of the payment processing attempt. Serialized as "status" in JSON format.
     * Expected values: "SUCCESS", "FAILED".
     */
    @JsonProperty("status")
    private String paymentStatus;
}