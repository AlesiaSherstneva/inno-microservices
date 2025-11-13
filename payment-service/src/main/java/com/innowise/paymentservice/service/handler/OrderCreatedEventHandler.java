package com.innowise.paymentservice.service.handler;

import com.innowise.paymentservice.model.dto.kafka.OrderCreatedEvent;
import com.innowise.paymentservice.model.entity.Payment;
import com.innowise.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = "${orders.events.topic}")
@RequiredArgsConstructor
public class OrderCreatedEventHandler {
    private final PaymentService paymentService;

    @KafkaHandler
    public void handleOrderCreatedEvent(OrderCreatedEvent event) {
        Payment createdPayment = paymentService.createPayment(event);


    }
}