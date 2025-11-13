package com.innowise.orderservice.model.entity.enums;

/**
 * Enumeration represents the possible states of an order in the system workflow.
 * <p>
 * Order lifecycle typically progresses:
 * NEW → PROCESSING → COMPLETED
 * </p>
 * <p>
 * CANCELLED can occur at any stage before COMPLETED
 * </p>
 */
public enum OrderStatus {
    PROCESSING, COMPLETED, CANCELLED
}