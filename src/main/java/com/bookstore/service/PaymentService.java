package com.bookstore.service;

import com.bookstore.domain.Order;
import com.bookstore.domain.Payment;
import com.bookstore.domain.PaymentMethod;
import com.bookstore.dto.PaymentResponse;

public interface PaymentService {
    Payment initiatePayment(Order order, PaymentMethod paymentMethod);
    PaymentResponse processWebPayment(Long paymentId, String transactionReference);
    PaymentResponse processUssdPayment(Long paymentId, String ussdCode);
    PaymentResponse processTransferPayment(Long paymentId, String transferReference);
    Payment getPaymentStatus(Long paymentId);
    void simulatePaymentCallback(Long paymentId, boolean successful);
}