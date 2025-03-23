package com.bookstore.dto;

import com.bookstore.domain.PaymentMethod;
import com.bookstore.domain.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private Long id;
    private Long orderId;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private String reference;
    private String message;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}