package com.innowise.orderservice.model.dto.mapper;

import com.innowise.orderservice.model.dto.OrderRequestDto;
import com.innowise.orderservice.model.entity.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    Order toEntity(Long userId, OrderRequestDto requestDto);
}