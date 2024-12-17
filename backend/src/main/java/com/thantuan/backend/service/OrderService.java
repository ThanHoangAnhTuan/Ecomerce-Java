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
        Payment payment = Payment.builder()
                .amount(orderRequest.getPaymentInfo().getAmount())
                .status(orderRequest.getPaymentInfo().getStatus())
                .method(orderRequest.getPaymentInfo().getMethod())
                .build();

        Order order = Order.builder()
                .buyer(user)
                .status(OrderStatus.PENDING)
                .payment(payment)
                .build();

        payment.setOrder(order);

        List<OrderItem> orderItemList = orderRequest.getOrderItemList().stream().map(orderItemRequest -> {
            Product product = productRepo.findById(orderItemRequest.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException("Product Not Found"));
            return OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(orderItemRequest.getQuantity())
                    .price(product.getPrice())
                    .seller(product.getUser())
                    .build();
        }).toList();

        BigDecimal totalPrice = BigDecimal.ZERO;

        for (OrderItem orderItem : orderItemList) {
            Product product = productRepo.findById(orderItem.getProduct().getId())
                    .orElseThrow(() -> new ProductNotFoundException("Product Not Found"));
            product.setStock(product.getStock() - orderItem.getQuantity());
            BigDecimal itemPrice = orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity()));
            totalPrice = totalPrice.add(itemPrice);
        }

        order.setTotal(totalPrice);
        order.setOrderItemList(orderItemList);

        orderRepo.save(order);
        paymentRepo.save(payment);
        orderRepo.save(order);

        return Response.builder()
                    .status(OK.value())
                    .message("Order was successfully")
                    .build();
    }

    public Response updateOrderStatus(Long orderId) throws IllegalAccessException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepo.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        Order order = orderRepo.findById(orderId).orElseThrow(() -> new UsernameNotFoundException("Order Not Found"));
        if (Objects.equals(order.getStatus().toString(), "CANCEL")) {
            throw new IllegalAccessException("You can not update order status when it is cancelled");
        }
        order.setStatus(order.getStatus().getNext());
        orderRepo.save(order);
        return Response.builder()
                    .status(OK.value())
                    .message("Order status updated successfully")
                    .build();
    }

    public Response cancelOrder(Long orderId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepo.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        Order order = orderRepo.findById(orderId).orElseThrow(() -> new UsernameNotFoundException("Order Not Found"));
        if (!order.getBuyer().equals(user)) {
            throw new UsernameNotFoundException("You are not the buyer of this order");
        }
        if (Objects.equals(order.getStatus().toString(), "PENDING")) {
            order.setStatus(OrderStatus.CANCELLED);
        }
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
        List<Order> orderList = orderRepo.findAllByBuyerId(user.getId());
        System.out.println(orderList);
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
        List<Order> orderList = orderRepo.findAllBySellerId(user.getId());
        List<OrderDto> orderDtoList = orderList.stream()
                                            .map(entityDtoMapper::mapOrderToOrderDto)
                                            .toList();
        return Response.builder()
                .status(OK.value())
                .orderList(orderDtoList)
                .message("Get order list successfully").build();
    }
}
