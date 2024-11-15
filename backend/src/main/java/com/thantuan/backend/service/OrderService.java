package com.thantuan.backend.service;

import com.thantuan.backend.dto.*;
import com.thantuan.backend.entity.*;
import com.thantuan.backend.enums.OrderStatus;
import com.thantuan.backend.exception.ProductNotFoundException;
import com.thantuan.backend.mapper.EntityDtoMapper;
import com.thantuan.backend.repository.IOrderRepo;
import com.thantuan.backend.repository.IPaymentRepo;
import com.thantuan.backend.repository.IProductRepo;
import com.thantuan.backend.repository.IUserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.OK;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {

    private final IOrderRepo orderRepo;
    private final IProductRepo productRepo;
    private final IUserRepo userRepo;
    private final EntityDtoMapper entityDtoMapper;
    private final IPaymentRepo paymentRepo;

    public Response placeOrder(OrderRequest orderRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepo.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        List<OrderItem> orderItemList = orderRequest.getOrderItemList().stream().map(orderItemRequest -> {
            Product product = productRepo.findById(orderItemRequest.getProductId())
                                        .orElseThrow(() -> new ProductNotFoundException("Product Not Found"));
            return OrderItem.builder()
                            .product(product)
                            .quantity(orderItemRequest.getQuantity())
                            .price(product.getPrice().multiply(BigDecimal.valueOf(orderItemRequest.getQuantity())))
                            .build();
        }).collect(Collectors.toList());

        BigDecimal totalPrice = BigDecimal.ZERO;

        for (OrderItemRequest item : orderRequest.getOrderItemList()) {
            Product product = productRepo.findById(item.getProductId())
                                        .orElseThrow(() -> new ProductNotFoundException("Product Not Found"));
            if (product != null) {
                BigDecimal itemPrice = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                totalPrice = totalPrice.add(itemPrice);
            }
        }
        Payment payment = Payment.builder()
                                .amount(orderRequest.getPaymentInfo().getAmount())
                                .status(orderRequest.getPaymentInfo().getStatus())
                                .method(orderRequest.getPaymentInfo().getMethod())
                                .build();
        paymentRepo.save(payment);

        Order order = Order.builder()
                .orderItemList(orderItemList)
                .total(totalPrice)
                .status(OrderStatus.PENDING)
                .user(user)
                .payment(payment)
                .build();
        orderItemList.forEach(orderItem -> orderItem.setOrder(order));
        orderRepo.save(order);
        return Response.builder()
                    .status(OK.value())
                    .message("Order was successfully")
                    .build();
    }

    public Response updateOrderItemStatus(Long orderId, String status) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepo.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        Order order = orderRepo.findById(orderId).orElseThrow(() -> new UsernameNotFoundException("Order Not Found"));
        if (!Objects.equals(order.getUser().getId(), user.getId())) {
            throw new UsernameNotFoundException("User not logged in");
        }
        order.setStatus(OrderStatus.valueOf(status.toUpperCase()));
        orderRepo.save(order);
        return Response.builder()
                    .status(OK.value())
                    .message("Order status updated successfully")
                    .build();
    }

    public Response getOrderBuyer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepo.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        List<Order> orderList = orderRepo.findAllByUserId(user.getId());
        List<OrderDto> orderDtoList = orderList.stream()
                                            .map(entityDtoMapper::mapOrderToOrderDto)
                                            .toList();
        return Response.builder()
                    .status(OK.value())
                    .orderList(orderDtoList)
                    .message("Get order list successfully").build();
    }

    public Response getOrderSeller() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepo.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        List<Order> orderList = orderRepo.findByUserEmail(user.getEmail());
        List<OrderDto> orderDtoList = orderList.stream()
                                            .map(entityDtoMapper::mapOrderToOrderDto)
                                            .toList();
        return Response.builder()
                .status(OK.value())
                .orderList(orderDtoList)
                .message("Get order list successfully").build();
    }
}
