package com.bookstore.dto;

import com.bookstore.domain.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentResponse {
    private Long paymentId;
    private PaymentStatus status;
    private String message;
    private String reference;
}