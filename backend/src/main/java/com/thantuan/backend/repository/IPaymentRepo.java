package com.thantuan.backend.repository;

import com.thantuan.backend.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IPaymentRepo extends JpaRepository<Payment, Long> {
}
