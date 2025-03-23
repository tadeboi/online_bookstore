package com.bookstore.controller;

import com.bookstore.domain.Order;
import com.bookstore.dto.CreateOrderRequest;
import com.bookstore.service.OrderService;
import com.bookstore.security.CurrentUser;
import com.bookstore.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> createOrder(
            @CurrentUser UserPrincipal currentUser,
            @Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.ok(orderService.createOrder(currentUser.getId(), request));
    }

    @GetMapping
    public ResponseEntity<List<Order>> getOrderHistory(@CurrentUser UserPrincipal currentUser) {
        return ResponseEntity.ok(orderService.getOrderHistory(currentUser.getId()));
    }
}