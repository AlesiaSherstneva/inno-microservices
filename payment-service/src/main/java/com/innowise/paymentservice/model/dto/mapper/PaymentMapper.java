package com.innowise.paymentservice.model.dto.mapper;

import com.innowise.paymentservice.model.dto.kafka.OrderCreatedEvent;
import com.innowise.paymentservice.model.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "timestamp", expression = "java(java.time.LocalDateTime.now())")
    Payment toEntity(OrderCreatedEvent event);
}