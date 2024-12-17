package com.thantuan.backend.repository;

import com.thantuan.backend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IOrderRepo extends JpaRepository<Order, Long> {
    List<Order> findAllByBuyerId(Long id);

    @Query(value = "SELECT DISTINCT o.* " +
            "FROM orders o " +
            "JOIN order_items oi ON o.id = oi.order_id " +
            "WHERE oi.seller_id = :id", nativeQuery = true)
    List<Order> findAllBySellerId(Long id);
}
