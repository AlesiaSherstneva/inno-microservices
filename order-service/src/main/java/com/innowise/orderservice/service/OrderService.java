package com.innowise.orderservice.service;

import com.innowise.orderservice.client.UserServiceClient;
import com.innowise.orderservice.exception.ResourceNotFoundException;
import com.innowise.orderservice.model.dto.CustomerDto;
import com.innowise.orderservice.model.dto.OrderItemRequestDto;
import com.innowise.orderservice.model.dto.OrderRequestDto;
import com.innowise.orderservice.model.dto.OrderResponseDto;
import com.innowise.orderservice.model.dto.mapper.OrderMapper;
import com.innowise.orderservice.model.entity.Order;
import com.innowise.orderservice.model.entity.OrderItem;
import com.innowise.orderservice.repository.ItemRepository;
import com.innowise.orderservice.repository.OrderRepository;
import com.innowise.orderservice.util.Constant;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final OrderMapper orderMapper;
    private final UserServiceClient userServiceClient;

    @Transactional(readOnly = true)
    public OrderResponseDto getOrderById(Long orderId, Long userId) {
        Order retrievedOrder = orderRepository.findOrderById(orderId)
                .orElseThrow(() -> ResourceNotFoundException.orderNotFound(orderId));

        if (!retrievedOrder.getUserId().equals(userId) && !isUserAdmin()) {
            throw new AccessDeniedException("You don't have permission to access this order");
        }

        return orderMapper.toDto(retrievedOrder, getCustomerInfoOrFallback(userId));
    }

    @Transactional
    public OrderResponseDto createOrder(Long userId, OrderRequestDto orderRequestDto) {
        Order newOrder = orderMapper.toEntity(userId, orderRequestDto);

        orderRequestDto.getItems().stream()
                .map(this::convertToOrderItem)
                .forEach(newOrder::addOrderItem);

        Order createdOrder = orderRepository.save(newOrder);

        return orderMapper.toDto(createdOrder, getCustomerInfoOrFallback(userId));
    }

    private boolean isUserAdmin() {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(Constant.ROLE_ADMIN));
    }

    private OrderItem convertToOrderItem(OrderItemRequestDto requestDto) {
        return OrderItem.builder()
                .item(itemRepository.findItemById(requestDto.getItemId())
                        .orElseThrow(() -> ResourceNotFoundException.itemNotFound(requestDto.getItemId())))
                .quantity(requestDto.getQuantity())
                .build();
    }

    private CustomerDto getCustomerInfoOrFallback(Long userId) {
        try {
            return userServiceClient.getUserById(userId);
        } catch (FeignException ex) {
            return CustomerDto.builder()
                    .errorMessage("User information temporary unavailable")
                    .build();
        }
    }
}