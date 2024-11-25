package com.thantuan.backend.controller;

import com.thantuan.backend.dto.OrderRequest;
import com.thantuan.backend.dto.Response;
import com.thantuan.backend.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderItemController {
    private final OrderService orderService;

    @GetMapping("/buyer")
    @PreAuthorize("hasAuthority('BUYER')")
    public ResponseEntity<Response> getOrderBuyer() {
        return ResponseEntity.ok(orderService.getOrderBuyer());
    }

    @GetMapping("/seller")
    @PreAuthorize("hasAuthority('SELLER')")
    public ResponseEntity<Response> getOrderSeller() {
        return ResponseEntity.ok(orderService.getOrderSeller());
    }

    @PostMapping("/buy-product")
    @PreAuthorize("hasAuthority('BUYER')")
    public ResponseEntity<Response> placeOrder(@Valid @RequestBody OrderRequest orderRequest) {
        return ResponseEntity.ok(orderService.placeOrder(orderRequest));
    }

    @PutMapping("/update-order-status/{orderId}")
    @PreAuthorize("hasAuthority('SELLER')")
    public ResponseEntity<Response> updateOrderItemStatus(@PathVariable Long orderId,
                                                          @RequestBody String status) {
        return ResponseEntity.ok(orderService.updateOrderItemStatus(orderId, status));
    }
}
