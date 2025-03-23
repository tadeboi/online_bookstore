package com.bookstore.service.impl;

import com.bookstore.domain.*;
import com.bookstore.dto.PaymentResponse;
import com.bookstore.exception.PaymentNotFoundException;
import com.bookstore.repository.PaymentRepository;
import com.bookstore.service.OrderService;
import com.bookstore.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderService orderService;

    @Override
    @Transactional
    public Payment initiatePayment(Order order, PaymentMethod paymentMethod) {
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentMethod(paymentMethod);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCreatedAt(LocalDateTime.now());

        // Generate payment details based on method
        switch (paymentMethod) {
            case WEB:
                payment.setTransactionReference(generateReference("WEB"));
                break;
            case USSD:
                payment.setUssdCode("*123*" + generateReference("USSD") + "#");
                break;
            case TRANSFER:
                payment.setBankName("Example Bank");
                payment.setBankAccountNumber("1234567890");
                payment.setTransferReference(generateReference("TRF"));
                break;
        }

        return paymentRepository.save(payment);
    }

    @Override
    @Transactional
    public PaymentResponse processWebPayment(Long paymentId, String transactionReference) {
        Payment payment = getPayment(paymentId);
        validatePaymentMethod(payment, PaymentMethod.WEB);

        // Simulate web payment processing
        payment.setStatus(PaymentStatus.PROCESSING);
        payment.setTransactionReference(transactionReference);
        paymentRepository.save(payment);

        // Simulate async payment processing
        simulateAsyncPaymentProcessing(paymentId);

        return new PaymentResponse(
            payment.getId(),
            payment.getStatus(),
            "Web payment processing initiated",
            payment.getTransactionReference()
        );
    }

    @Override
    @Transactional
    public PaymentResponse processUssdPayment(Long paymentId, String ussdCode) {
        Payment payment = getPayment(paymentId);
        validatePaymentMethod(payment, PaymentMethod.USSD);

        // Simulate USSD payment processing
        payment.setStatus(PaymentStatus.PROCESSING);
        payment.setUssdCode(ussdCode);
        paymentRepository.save(payment);

        return new PaymentResponse(
            payment.getId(),
            payment.getStatus(),
            "Dial " + payment.getUssdCode() + " to complete payment",
            payment.getUssdCode()
        );
    }

    @Override
    @Transactional
    public PaymentResponse processTransferPayment(Long paymentId, String transferReference) {
        Payment payment = getPayment(paymentId);
        validatePaymentMethod(payment, PaymentMethod.TRANSFER);

        // Simulate bank transfer processing
        payment.setStatus(PaymentStatus.PROCESSING);
        payment.setTransferReference(transferReference);
        paymentRepository.save(payment);

        return new PaymentResponse(
            payment.getId(),
            payment.getStatus(),
            String.format("Transfer %s to account %s at %s with reference: %s",
                payment.getAmount(),
                payment.getBankAccountNumber(),
                payment.getBankName(),
                payment.getTransferReference()),
            payment.getTransferReference()
        );
    }

    @Override
    @Transactional
    public void simulatePaymentCallback(Long paymentId, boolean successful) {
        Payment payment = getPayment(paymentId);
        payment.setStatus(successful ? PaymentStatus.COMPLETED : PaymentStatus.FAILED);
        payment.setCompletedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        if (successful) {
            orderService.updateOrderStatus(payment.getOrder().getId(), OrderStatus.PAID);
        }
    }

    @Override
    public Payment getPaymentStatus(Long paymentId) {
        return getPayment(paymentId);
    }

    private Payment getPayment(Long paymentId) {
        return paymentRepository.findById(paymentId)
            .orElseThrow(() -> new PaymentNotFoundException("Payment not found"));
    }

    private void validatePaymentMethod(Payment payment, PaymentMethod expectedMethod) {
        if (payment.getPaymentMethod() != expectedMethod) {
            throw new IllegalStateException("Invalid payment method");
        }
    }

    private String generateReference(String prefix) {
        return prefix + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private void simulateAsyncPaymentProcessing(Long paymentId) {
        // Simulate async processing with a separate thread
        new Thread(() -> {
            try {
                Thread.sleep(5000); // Simulate 5-second processing time
                simulatePaymentCallback(paymentId, Math.random() > 0.1); // 90% success rate
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
}