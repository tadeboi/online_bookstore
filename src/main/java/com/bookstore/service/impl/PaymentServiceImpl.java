package com.bookstore.service.impl;

import com.bookstore.domain.*;
import com.bookstore.dto.PaymentResponse;
import com.bookstore.exception.PaymentNotFoundException;
import com.bookstore.repository.PaymentRepository;
import com.bookstore.service.OrderService;
import com.bookstore.service.PaymentService;
import com.bookstore.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
        payment.setCreatedAt(DateTimeUtil.getCurrentUtcDateTime());

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
        Payment payment = getPaymentById(paymentId);
        validatePaymentMethod(payment, PaymentMethod.WEB);

        payment.setStatus(PaymentStatus.PROCESSING);
        payment.setTransactionReference(transactionReference);
        paymentRepository.save(payment);

        // Simulate async payment processing
        simulatePaymentProcessing(paymentId);

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
        Payment payment = getPaymentById(paymentId);
        validatePaymentMethod(payment, PaymentMethod.USSD);

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
        Payment payment = getPaymentById(paymentId);
        validatePaymentMethod(payment, PaymentMethod.TRANSFER);

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
    @Transactional(readOnly = true)
    public Payment getPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId)
            .orElseThrow(() -> new PaymentNotFoundException("Payment not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Payment> getUserPayments(Long userId) {
        return paymentRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public void handlePaymentCallback(Long paymentId, boolean successful) {
        Payment payment = getPaymentById(paymentId);
        payment.setStatus(successful ? PaymentStatus.COMPLETED : PaymentStatus.FAILED);
        payment.setCompletedAt(DateTimeUtil.getCurrentUtcDateTime());
        paymentRepository.save(payment);

        if (successful) {
            orderService.updateOrderStatus(payment.getOrder().getId(), OrderStatus.PAID);
        }
    }

    private void validatePaymentMethod(Payment payment, PaymentMethod expectedMethod) {
        if (payment.getPaymentMethod() != expectedMethod) {
            throw new IllegalStateException("Invalid payment method");
        }
    }

    private String generateReference(String prefix) {
        return prefix + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private void simulatePaymentProcessing(Long paymentId) {
        // In a real application, this would be handled by a separate thread or message queue
        new Thread(() -> {
            try {
                Thread.sleep(5000); // Simulate 5-second processing time
                handlePaymentCallback(paymentId, Math.random() > 0.1); // 90% success rate
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
}