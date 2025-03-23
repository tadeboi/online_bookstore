@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    // ... other code ...

    @PostMapping("/{orderId}/web")
    public ResponseEntity<PaymentResponse> processWebPayment(
            @PathVariable Long orderId,
            @RequestParam String transactionReference) {
        Order order = orderService.getOrderById(orderId);  // Updated method call
        Payment payment = paymentService.initiatePayment(order, PaymentMethod.WEB);
        return ResponseEntity.ok(paymentService.processWebPayment(payment.getId(), transactionReference));
    }

    // ... rest of the code ...
}