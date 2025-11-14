package com.innowise.paymentservice.model.dto.kafka;

import com.innowise.paymentservice.model.entity.enums.PaymentStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentProcessedEvent {
    private Long orderId;
    private PaymentStatus status;
}