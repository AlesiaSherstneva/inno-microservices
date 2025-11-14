package com.innowise.paymentservice.service.handler;

import com.innowise.paymentservice.model.dto.kafka.OrderCreatedEvent;
import com.innowise.paymentservice.model.dto.kafka.PaymentProcessedEvent;
import com.innowise.paymentservice.model.dto.mapper.PaymentMapper;
import com.innowise.paymentservice.model.entity.Payment;
import com.innowise.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = "${orders.events.topic}")
@RequiredArgsConstructor
public class OrderCreatedEventHandler {
    private final PaymentService paymentService;
    private final PaymentMapper paymentMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${payments.events.topic}")
    private String paymentsEventsTopic;

    @KafkaHandler
    public void handleOrderCreatedEvent(OrderCreatedEvent orderCreatedEvent) {
        Payment createdPayment = paymentService.createPayment(orderCreatedEvent);

        PaymentProcessedEvent paymentProcessedEvent = paymentMapper.toEvent(createdPayment);
        kafkaTemplate.send(paymentsEventsTopic, paymentProcessedEvent);
    }
}