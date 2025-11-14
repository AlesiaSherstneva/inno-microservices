package com.innowise.paymentservice.service;

import com.innowise.paymentservice.model.dto.kafka.OrderCreatedEvent;
import com.innowise.paymentservice.model.entity.Payment;

public interface PaymentService {
    Payment createPayment(OrderCreatedEvent event);
}