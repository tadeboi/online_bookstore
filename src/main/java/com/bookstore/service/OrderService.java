package com.bookstore.service;

import com.bookstore.domain.Order;
import com.bookstore.domain.OrderStatus;
import com.bookstore.dto.CreateOrderRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {
    Order createOrder(Long userId, CreateOrderRequest request);
    Order getOrderById(Long orderId);
    Page<Order> getUserOrders(Long userId, Pageable pageable);
    Order updateOrderStatus(Long orderId, OrderStatus status);
    List<Order> getOrdersByStatus(OrderStatus status);
    List<Order> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    void cancelOrder(Long orderId);
}