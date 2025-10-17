package com.innowise.orderservice.service;

import com.innowise.orderservice.client.UserServiceClient;
import com.innowise.orderservice.model.dto.CustomerDto;
import com.innowise.orderservice.model.dto.OrderItemRequestDto;
import com.innowise.orderservice.model.dto.OrderRequestDto;
import com.innowise.orderservice.model.dto.OrderResponseDto;
import com.innowise.orderservice.model.dto.mapper.OrderMapper;
import com.innowise.orderservice.model.entity.Order;
import com.innowise.orderservice.model.entity.OrderItem;
import com.innowise.orderservice.repository.ItemRepository;
import com.innowise.orderservice.repository.OrderRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final OrderMapper orderMapper;
    private final UserServiceClient userServiceClient;

    @Transactional
    public OrderResponseDto createOrder(Long userId, OrderRequestDto orderRequestDto) {
        Order newOrder = orderMapper.toEntity(userId, orderRequestDto);

        orderRequestDto.getItems().stream()
                .map(this::convertToOrderItem)
                .forEach(newOrder::addOrderItem);

        Order createdOrder = orderRepository.save(newOrder);

        CustomerDto customer = getCustomerInfoOrFallback(userId);

        return orderMapper.toDto(createdOrder, customer);
    }

    private OrderItem convertToOrderItem(OrderItemRequestDto requestDto) {
        return OrderItem.builder()
                .item(itemRepository.findItemById(requestDto.getItemId())
                        .orElseThrow(() -> new RuntimeException("Item not found")))
                .quantity(requestDto.getQuantity())
                .build();
    }

    private CustomerDto getCustomerInfoOrFallback(Long userId) {
        try {
            return userServiceClient.getUserById(userId);
        } catch (FeignException ex) {
            return CustomerDto.builder()
                    .errorMessage("User information temporarily unavailable")
                    .build();
        }
    }
}