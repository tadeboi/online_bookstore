package com.bookstore.service;

import com.bookstore.domain.Order;
import com.bookstore.dto.CreateOrderRequest;
import java.util.List;

public interface OrderService {
    Order createOrder(Long userId, CreateOrderRequest request);
    List<Order> getOrderHistory(Long userId);
    Order getOrderById(Long orderId, Long userId);
    void updateOrderStatus(Long orderId, OrderStatus status);
}