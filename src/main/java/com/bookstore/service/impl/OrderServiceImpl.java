// ... existing imports ...

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    // ... existing code ...

    @Override
    public Order getOrderById(Long orderId) {  // Updated method signature
        return orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException("Order not found"));
    }

    // ... rest of the existing code ...
}