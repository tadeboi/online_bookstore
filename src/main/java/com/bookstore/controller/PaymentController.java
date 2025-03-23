package com.bookstore.controller;

import com.bookstore.domain.Order;
import com.bookstore.domain.Payment;
import com.bookstore.dto.PaymentResponse;
import com.bookstore.service.OrderService;
import com.bookstore.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final OrderService orderService;

    @PostMapping("/{orderId}/web")
    public ResponseEntity<PaymentResponse> processWebPayment(
            @PathVariable Long orderId,
            @RequestParam String transactionReference) {
        Order order = orderService.getOrderById(orderId);
        Payment payment = paymentService.initiatePayment(order, PaymentMethod.WEB);
        return ResponseEntity.ok(paymentService.processWebPayment(payment.getId(), transactionReference));
    }

    @PostMapping("/{orderId}/ussd")
    public ResponseEntity<PaymentResponse> processUssdPayment(
            @PathVariable Long orderId,
            @RequestParam String ussdCode) {
        Order order = orderService.getOrderById(orderId);
        Payment payment = paymentService.initiatePayment(order, PaymentMethod.USSD);
        return ResponseEntity.ok(paymentService.processUssdPayment(payment.getId(), ussdCode));
    }

    @PostMapping("/{orderId}/transfer")
    public ResponseEntity<PaymentResponse> processTransferPayment(
            @PathVariable Long orderId,
            @RequestParam String transferReference) {
        Order order = orderService.getOrderById(orderId);
        Payment payment = paymentService.initiatePayment(order, PaymentMethod.TRANSFER);
        return ResponseEntity.ok(paymentService.processTransferPayment(payment.getId(), transferReference));
    }

    @GetMapping("/{paymentId}/status")
    public ResponseEntity<Payment> getPaymentStatus(@PathVariable Long paymentId) {
        return ResponseEntity.ok(paymentService.getPaymentStatus(paymentId));
    }
}