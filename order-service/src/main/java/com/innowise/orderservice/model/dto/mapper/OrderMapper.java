package com.innowise.orderservice.model.dto.mapper;

import com.innowise.orderservice.model.dto.OrderRequestDto;
import com.innowise.orderservice.model.dto.OrderResponseDto;
import com.innowise.orderservice.model.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(componentModel = "spring",
        uses = OrderItemMapper.class)
public interface OrderMapper {
    Order toEntity(Long userId, OrderRequestDto requestDto);

    @Mapping(target = "items", source = "orderItems")
    @Mapping(target = "totalPrice", expression = "java(calculateTotalPrice(order))")
    OrderResponseDto toDto(Order order);

    default BigDecimal calculateTotalPrice(Order order) {
        if (order.getOrderItems() == null) {
            return BigDecimal.ZERO;
        }
        return order.getOrderItems().stream()
                .map(orderItem -> orderItem.getItem().getPrice()
                        .multiply(BigDecimal.valueOf(orderItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}