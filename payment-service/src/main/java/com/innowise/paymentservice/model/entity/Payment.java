package com.innowise.paymentservice.model.entity;

import com.innowise.paymentservice.model.entity.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Document(collection = "payments")
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    private String id;

    @Field(value = "order_id")
    private Long orderId;

    @Field(value = "user_id")
    private Long userId;

    @Field(value = "status", targetType = FieldType.STRING)
    private PaymentStatus status;

    @Field(value = "timestamp")
    private LocalDateTime timestamp;

    @Field(value = "payment_amount")
    private BigDecimal paymentAmount;
}