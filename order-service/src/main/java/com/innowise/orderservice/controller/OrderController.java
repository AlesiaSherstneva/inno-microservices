package com.innowise.orderservice.controller;

import com.innowise.orderservice.model.dto.OrderRequestDto;
import com.innowise.orderservice.model.dto.OrderResponseDto;
import com.innowise.orderservice.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody @Valid OrderRequestDto requestDto,
                                                        @AuthenticationPrincipal Long userId) {
        OrderResponseDto createdOrder = orderService.createOrder(userId, requestDto);

        return ResponseEntity.ok(createdOrder);
    }
}