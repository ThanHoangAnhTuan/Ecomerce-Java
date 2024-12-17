package com.thantuan.backend.enums;

public enum OrderStatus {
    PENDING,
    CONFIRMED,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    RETURNED;

    public OrderStatus getNext() {
        return switch (this) {
            case PENDING -> CONFIRMED;
            case CONFIRMED -> SHIPPED;
            case SHIPPED -> DELIVERED;
            case DELIVERED -> RETURNED;
            default -> throw new IllegalStateException("Cannot move to the next status from " + this);
        };
    }
}
