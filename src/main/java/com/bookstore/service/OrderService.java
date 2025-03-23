package com.bookstore.service;

import com.bookstore.domain.Order;
import com.bookstore.domain.OrderStatus;
import com.bookstore.dto.CreateOrderRequest;
import java.util.List;

public interface OrderService {
    Order createOrder(Long userId, CreateOrderRequest request);
    List<Order> getOrderHistory(Long userId);
    Order getOrderById(Long orderId);  // Changed to accept only orderId
    void updateOrderStatus(Long orderId, OrderStatus status);
}