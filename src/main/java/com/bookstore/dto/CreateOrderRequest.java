package com.bookstore.dto;

import com.bookstore.domain.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateOrderRequest {
    @NotNull(message = "Payment method cannot be null")
    private PaymentMethod paymentMethod;

    private String shippingAddress;
    private String notes;
}