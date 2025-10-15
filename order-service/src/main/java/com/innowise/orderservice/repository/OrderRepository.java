package com.innowise.orderservice.repository;

import com.innowise.orderservice.model.entity.Order;
import com.innowise.orderservice.model.entity.enums.OrderStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @EntityGraph(value = "Order.withItems")
    Optional<Order> findOrderById(Long id);

    @EntityGraph(value = "Order.withItems")
    List<Order> findOrdersByIdIn(List<Long> ids);

    @EntityGraph(value = "Order.withItems")
    List<Order> findOrdersByStatusIn(List<OrderStatus> statuses);
}