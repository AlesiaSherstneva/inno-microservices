package com.innowise.orderservice.util;

import com.innowise.orderservice.model.dto.CustomerDto;
import com.innowise.orderservice.model.dto.OrderItemRequestDto;
import com.innowise.orderservice.model.dto.OrderRequestDto;
import com.innowise.orderservice.model.entity.Item;
import com.innowise.orderservice.model.entity.Order;
import com.innowise.orderservice.model.entity.OrderItem;
import com.innowise.orderservice.model.entity.enums.OrderStatus;

import java.math.BigDecimal;
import java.util.List;

public class DtoBuilder {
    public static OrderRequestDto buildOrderRequestDto() {
        return OrderRequestDto.builder()
                .items(List.of(OrderItemRequestDto.builder()
                        .itemId(TestConstant.INTEGER_ID)
                        .build()))
                .build();
    }

    public static Order buildOrder() {
        return Order.builder()
                .id(TestConstant.LONG_ID)
                .userId(TestConstant.LONG_ID)
                .status(OrderStatus.NEW)
                .creationDate(TestConstant.LOCAL_DATE_TIME_NOW)
                .build();
    }

    public static OrderItem buildOrderItem() {
        return OrderItem.builder()
                .order(buildOrder())
                .item(buildItem())
                .quantity(TestConstant.ITEM_QUANTITY)
                .build();
    }

    public static Item buildItem() {
        return Item.builder()
                .id(TestConstant.INTEGER_ID)
                .name(TestConstant.ITEM_NAME)
                .price(BigDecimal.TEN)
                .build();
    }

    public static CustomerDto buildCorrectCustomer() {
        return CustomerDto.builder()
                .userId(TestConstant.LONG_ID)
                .name(TestConstant.USER_NAME)
                .surname(TestConstant.USER_NAME)
                .email(TestConstant.USER_EMAIL)
                .build();
    }

    public static CustomerDto buildFailedCustomer() {
        return CustomerDto.builder()
                .errorMessage("User information temporary unavailable")
                .build();
    }
}