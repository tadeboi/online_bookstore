package com.bookstore.dto;

import com.bookstore.domain.PaymentMethod;
import com.bookstore.domain.PaymentStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class PaymentResponse {
    private Long id;
    private Long orderId;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private String transactionReference;
    private String message;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    // Constructor for simple responses
    public PaymentResponse(Long id, PaymentStatus status, String transactionReference, String message) {
        this.id = id;
        this.status = status;
        this.transactionReference = transactionReference;
        this.message = message;
    }

    // Constructor for full payment details
    public PaymentResponse(Long id, Long orderId, BigDecimal amount, PaymentMethod paymentMethod,
                         PaymentStatus status, String transactionReference, String message,
                         LocalDateTime createdAt, LocalDateTime completedAt) {
        this.id = id;
        this.orderId = orderId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.transactionReference = transactionReference;
        this.message = message;
        this.createdAt = createdAt;
        this.completedAt = completedAt;
    }
}