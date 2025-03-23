package com.bookstore.service;

import com.bookstore.domain.*;
import com.bookstore.dto.CreateOrderRequest;
import com.bookstore.exception.CartNotFoundException;
import com.bookstore.exception.OrderNotFoundException;
import com.bookstore.repository.CartRepository;
import com.bookstore.repository.OrderRepository;
import com.bookstore.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private CartRepository cartRepository;

    private OrderService orderService;
    private User testUser;
    private Cart testCart;
    private Book testBook;

    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl(orderRepository, cartRepository);
        
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testBook = new Book();
        testBook.setId(1L);
        testBook.setPrice(29.99);

        testCart = new Cart();
        testCart.setId(1L);
        testCart.setUser(testUser);
        
        CartItem cartItem = new CartItem();
        cartItem.setBook(testBook);
        cartItem.setQuantity(2);
        cartItem.setCart(testCart);
        
        testCart.setItems(new ArrayList<>(List.of(cartItem)));
    }

    @Test
    void whenCreateOrder_withValidCart_thenSuccess() {
        // Arrange
        CreateOrderRequest request = new CreateOrderRequest();
        request.setPaymentMethod(PaymentMethod.WEB);

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Order result = orderService.createOrder(1L, request);

        // Assert
        assertNotNull(result);
        assertEquals(PaymentMethod.WEB, result.getPaymentMethod());
        assertEquals(OrderStatus.PENDING, result.getStatus());
        assertEquals(59.98, result.getTotalAmount());
        verify(cartRepository).delete(testCart);
    }

    @Test
    void whenCreateOrder_withEmptyCart_thenThrowException() {
        // Arrange
        testCart.setItems(new ArrayList<>());
        CreateOrderRequest request = new CreateOrderRequest();
        request.setPaymentMethod(PaymentMethod.WEB);

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));

        // Act & Assert
        assertThrows(IllegalStateException.class, 
            () -> orderService.createOrder(1L, request));
    }

    @Test
    void whenGetOrderHistory_thenReturnOrders() {
        // Arrange
        Order order = new Order();
        order.setId(1L);
        order.setUser(testUser);
        order.setOrderDate(LocalDateTime.now());

        when(orderRepository.findByUserIdOrderByOrderDateDesc(1L))
            .thenReturn(List.of(order));

        // Act
        List<Order> results = orderService.getOrderHistory(1L);

        // Assert
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals(1L, results.get(0).getId());
    }

    @Test
    void whenUpdateOrderStatus_withValidOrder_thenSuccess() {
        // Arrange
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Act
        orderService.updateOrderStatus(1L, OrderStatus.PAID);

        // Assert
        assertEquals(OrderStatus.PAID, order.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void whenUpdateOrderStatus_withInvalidOrder_thenThrowException() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OrderNotFoundException.class, 
            () -> orderService.updateOrderStatus(1L, OrderStatus.PAID));
    }
}