package com.innowise.orderservice.service.handler;

import com.innowise.orderservice.exception.ResourceNotFoundException;
import com.innowise.orderservice.model.dto.kafka.PaymentProcessedEvent;
import com.innowise.orderservice.model.entity.Order;
import com.innowise.orderservice.model.entity.enums.OrderStatus;
import com.innowise.orderservice.repository.OrderRepository;
import com.innowise.orderservice.util.Constant;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = "${payments.events.topic}")
@RequiredArgsConstructor
public class PaymentProcessedEventHandler {
    private final OrderRepository orderRepository;

    @KafkaHandler
    public void handlePaymentProcessedEvent(PaymentProcessedEvent event) {
        Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(() -> ResourceNotFoundException.orderNotFound(event.getOrderId()));

        if (event.getPaymentStatus().equals(Constant.PAYMENT_SERVICE_SUCCESS)) {
            order.setStatus(OrderStatus.COMPLETED);
        } else if (event.getPaymentStatus().equals(Constant.PAYMENT_SERVICE_FAILED)) {
            order.setStatus(OrderStatus.PAYMENT_FAILED);
        }

        orderRepository.save(order);
    }
}