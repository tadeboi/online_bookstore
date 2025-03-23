package com.bookstore.dto;

import com.bookstore.domain.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateOrderRequest {
    @NotNull
    private PaymentMethod paymentMethod;
}