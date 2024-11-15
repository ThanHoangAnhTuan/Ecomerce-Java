package com.thantuan.backend.repository;

import com.thantuan.backend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IOrderRepo extends JpaRepository<Order, Long> {
    List<Order> findAllByUserId(Long id);

    List<Order> findByUserEmail(String email);
}
