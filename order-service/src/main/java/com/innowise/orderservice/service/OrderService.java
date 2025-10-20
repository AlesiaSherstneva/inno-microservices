package com.innowise.orderservice.service;

import com.innowise.orderservice.model.dto.OrderRequestDto;
import com.innowise.orderservice.model.dto.OrderResponseDto;
import com.innowise.orderservice.model.entity.enums.OrderStatus;

import java.util.List;

public interface OrderService {
    OrderResponseDto getOrderById(Long orderId, Long userId);

    List<OrderResponseDto> getOrdersByIds(List<Long> orderIds);

    List<OrderResponseDto> getOrdersByStatuses(List<OrderStatus> statuses);

    OrderResponseDto createOrder(Long userId, OrderRequestDto orderRequestDto);

    OrderResponseDto updateOrder(Long orderId, OrderRequestDto orderRequestDto, Long userId);

    void cancelOrDeleteOrder(Long orderId, Long userId);
}