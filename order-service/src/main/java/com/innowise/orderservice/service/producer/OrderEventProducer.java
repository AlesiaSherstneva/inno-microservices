package com.innowise.orderservice.service.producer;

import com.innowise.orderservice.model.dto.kafka.OrderCreatedEvent;
import com.innowise.orderservice.model.dto.mapper.OrderMapper;
import com.innowise.orderservice.model.entity.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventProducer {
    private final OrderMapper orderMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${orders.events.topic}")
    private String ordersEventsTopic;

    public void sendOrderEvent(Order createdOrder) {
        OrderCreatedEvent event = orderMapper.toEvent(createdOrder);

        kafkaTemplate.send(ordersEventsTopic, event)
                .whenComplete((result, exception) -> {
                    if (exception == null) {
                        log.info("Payment request for order with id {} has been sent successfully", createdOrder.getId());
                    } else {
                        log.warn("Failed to send payment request for order with id {}: {}",
                                createdOrder.getId(), exception.getMessage());
                    }
                });
    }
}