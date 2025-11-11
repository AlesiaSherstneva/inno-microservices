package com.innowise.orderservice.model.dto.kafka;

import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
public class OrderCreatedEvent implements Serializable {
    @Serial
    private static final long serialVersionUID = 2286755482808630515L;

    private Long orderId;
    private Long userId;
    private BigDecimal paymentAmount;
}