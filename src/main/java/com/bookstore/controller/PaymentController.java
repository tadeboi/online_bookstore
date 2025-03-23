package com.bookstore.controller;

import com.bookstore.domain.Order;
import com.bookstore.domain.Payment;
import com.bookstore.domain.PaymentMethod;
import com.bookstore.dto.PaymentResponse;
import com.bookstore.service.OrderService;
import com.bookstore.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController extends BaseController {
    private final PaymentService paymentService;
    private final OrderService orderService;

    @PostMapping("/{orderId}/web")
    public ResponseEntity<PaymentResponse> processWebPayment(
            @PathVariable Long orderId,
            @RequestParam String transactionReference) {
        Order order = orderService.getOrderById(orderId);
        Payment payment = paymentService.initiatePayment(order, PaymentMethod.WEB);
        return ok(paymentService.processWebPayment(payment.getId(), transactionReference));
    }

    @PostMapping("/{orderId}/ussd")
    public ResponseEntity<PaymentResponse> processUssdPayment(
            @PathVariable Long orderId,
            @RequestParam String ussdCode) {
        Order order = orderService.getOrderById(orderId);
        Payment payment = paymentService.initiatePayment(order, PaymentMethod.USSD);
        return ok(paymentService.processUssdPayment(payment.getId(), ussdCode));
    }

    @PostMapping("/{orderId}/transfer")
    public ResponseEntity<PaymentResponse> processTransferPayment(
            @PathVariable Long orderId,
            @RequestParam String transferReference) {
        Order order = orderService.getOrderById(orderId);
        Payment payment = paymentService.initiatePayment(order, PaymentMethod.TRANSFER);
        return ok(paymentService.processTransferPayment(payment.getId(), transferReference));
    }

    @GetMapping("/user")
    public ResponseEntity<List<Payment>> getUserPayments() {
        return ok(paymentService.getUserPayments(getCurrentUserId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPayment(@PathVariable Long id) {
        return ok(paymentService.getPaymentById(id));
    }

    // This endpoint would typically be called by the payment gateway
    @PostMapping("/{id}/callback")
    public ResponseEntity<Void> handlePaymentCallback(
            @PathVariable Long id,
            @RequestParam boolean successful) {
        paymentService.handlePaymentCallback(id, successful);
        return ResponseEntity.noContent().build();
    }
}