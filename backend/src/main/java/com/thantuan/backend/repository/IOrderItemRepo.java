package com.thantuan.backend.repository;

import com.thantuan.backend.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IOrderItemRepo extends JpaRepository<OrderItem,Long> {
}
