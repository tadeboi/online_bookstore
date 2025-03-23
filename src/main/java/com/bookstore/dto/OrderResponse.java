package com.bookstore.dto;

import com.bookstore.domain.OrderStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private Long id;
    private Long userId;
    private List<OrderItemResponse> items;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private LocalDateTime orderDate;
    private String paymentStatus;

    @Data
    public static class OrderItemResponse {
        private Long id;
        private Long bookId;
        private String bookTitle;
        private BigDecimal priceAtPurchase;
        private Integer quantity;
        private BigDecimal subtotal;
    }
}