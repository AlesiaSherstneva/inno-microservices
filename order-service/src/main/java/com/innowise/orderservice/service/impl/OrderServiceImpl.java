package com.innowise.orderservice.service.impl;

import com.innowise.orderservice.client.UserServiceClient;
import com.innowise.orderservice.exception.OrderStatusException;
import com.innowise.orderservice.exception.ResourceNotFoundException;
import com.innowise.orderservice.model.dto.CustomerDto;
import com.innowise.orderservice.model.dto.OrderItemRequestDto;
import com.innowise.orderservice.model.dto.OrderRequestDto;
import com.innowise.orderservice.model.dto.OrderResponseDto;
import com.innowise.orderservice.model.dto.mapper.OrderMapper;
import com.innowise.orderservice.model.entity.Order;
import com.innowise.orderservice.model.entity.OrderItem;
import com.innowise.orderservice.model.entity.enums.OrderStatus;
import com.innowise.orderservice.repository.ItemRepository;
import com.innowise.orderservice.repository.OrderRepository;
import com.innowise.orderservice.service.OrderService;
import com.innowise.orderservice.util.Constant;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final OrderMapper orderMapper;
    private final UserServiceClient userServiceClient;

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDto getOrderById(Long orderId, Long userId) {
        Order retrievedOrder = orderRepository.findOrderById(orderId)
                .orElseThrow(() -> ResourceNotFoundException.orderNotFound(orderId));

        if (!retrievedOrder.getUserId().equals(userId) && !isUserAdmin()) {
            throw new AccessDeniedException("You don't have permission to access this order");
        }

        return orderMapper.toDto(retrievedOrder, getCustomerInfoOrFallback(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getOrdersByIds(List<Long> orderIds) {
        List<Order> retrievedOrders = orderRepository.findOrdersByIdIn(orderIds);

        List<Long> userIds = retrievedOrders.stream()
                .map(Order::getUserId)
                .distinct()
                .toList();

        return orderMapper.toResponseDtoList(retrievedOrders, getCustomersInfoOrFallbackMap(userIds));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getOrdersByStatuses(List<OrderStatus> statuses) {
        List<Order> retrievedOrders = orderRepository.findOrdersByStatusIn(statuses);

        List<Long> userIds = retrievedOrders.stream()
                .map(Order::getUserId)
                .distinct()
                .toList();

        return orderMapper.toResponseDtoList(retrievedOrders, getCustomersInfoOrFallbackMap(userIds));
    }

    @Override
    @Transactional
    public OrderResponseDto createOrder(Long userId, OrderRequestDto orderRequestDto) {
        Order newOrder = orderMapper.toEntity(userId, orderRequestDto);

        orderRequestDto.getItems().stream()
                .map(this::convertToOrderItem)
                .forEach(newOrder::addOrderItem);

        Order createdOrder = orderRepository.save(newOrder);

        return orderMapper.toDto(createdOrder, getCustomerInfoOrFallback(userId));
    }

    @Override
    @Transactional
    public OrderResponseDto updateOrder(Long orderId, OrderRequestDto orderRequestDto, Long userId) {
        Order orderToUpdate = orderRepository.findOrderById(orderId)
                .orElseThrow(() -> ResourceNotFoundException.orderNotFound(orderId));

        if (!orderToUpdate.getUserId().equals(userId) && !isUserAdmin()) {
            throw new AccessDeniedException("You don't have permission to access this order");
        }

        orderToUpdate.getOrderItems().clear();
        orderRequestDto.getItems().stream()
                .map(this::convertToOrderItem)
                .forEach(orderToUpdate::addOrderItem);

        Order updatedOrder = orderRepository.save(orderToUpdate);

        return orderMapper.toDto(updatedOrder, getCustomerInfoOrFallback(userId));
    }

    @Override
    @Transactional
    public void cancelOrDeleteOrder(Long orderId, Long userId) {
        Order orderToDelete = orderRepository.findOrderById(orderId)
                .orElseThrow(() -> ResourceNotFoundException.orderNotFound(orderId));

        if (!orderToDelete.getUserId().equals(userId) && !isUserAdmin()) {
            throw new AccessDeniedException("You don't have permission to access this order");
        }

        if (isUserAdmin()) {
            orderRepository.deleteOrderAsAdmin(orderId);
        } else {
            if (orderToDelete.getStatus().equals(OrderStatus.CANCELLED)) {
                throw OrderStatusException.orderIsAlreadyCancelled(orderId);
            }
            if (orderToDelete.getStatus().equals(OrderStatus.COMPLETED)) {
                throw OrderStatusException.orderIsCompleted(orderId);
            }
            orderRepository.cancelOrderAsUser(orderId);
        }
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

    private Map<Long, CustomerDto> getCustomersInfoOrFallbackMap(List<Long> userIds) {
        if (userIds.isEmpty()) {
            return Collections.emptyMap();
        }

        try {
            List<CustomerDto> customers = userServiceClient.getUsersByIds(userIds);
            return customers.stream()
                    .collect(Collectors.toMap(CustomerDto::getUserId, Function.identity()));
        } catch (FeignException ex) {
            CustomerDto unavailableCustomer = CustomerDto.builder()
                    .errorMessage("User information temporary unavailable")
                    .build();

            return userIds.stream()
                    .collect(Collectors.toMap(Function.identity(), userId -> unavailableCustomer));
        }
    }
}