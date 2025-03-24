package com.bookstore.service.impl;

import com.bookstore.domain.*;
import com.bookstore.dto.CreateOrderRequest;
import com.bookstore.exception.InsufficientStockException;
import com.bookstore.exception.OrderNotFoundException;
import com.bookstore.repository.OrderRepository;
import com.bookstore.service.BookService;
import com.bookstore.service.CartService;
import com.bookstore.util.DateTimeUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartService cartService;

    @Mock
    private BookService bookService;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Captor
    private ArgumentCaptor<Order> orderCaptor;

    private Cart testCart;
    private Book testBook1;
    private Book testBook2;
    private Order testOrder;
    private LocalDateTime fixedDateTime;
    private MockedStatic<DateTimeUtil> dateTimeUtilMock;

    @BeforeEach
    void setUp() {
        fixedDateTime = LocalDateTime.of(2025, 3, 24, 10, 0);
        dateTimeUtilMock = Mockito.mockStatic(DateTimeUtil.class);
        dateTimeUtilMock.when(DateTimeUtil::getCurrentUtcDateTime).thenReturn(fixedDateTime);

        // Create books
        testBook1 = new Book();
        testBook1.setId(1L);
        testBook1.setTitle("Test Book 1");
        testBook1.setPrice(new BigDecimal("29.99"));
        testBook1.setStockQuantity(10);

        testBook2 = new Book();
        testBook2.setId(2L);
        testBook2.setTitle("Test Book 2");
        testBook2.setPrice(new BigDecimal("39.99"));
        testBook2.setStockQuantity(5);

        // Create cart with items
        testCart = new Cart();
        testCart.setId(1L);
        testCart.setItems(new ArrayList<>());

        CartItem cartItem1 = new CartItem();
        cartItem1.setBook(testBook1);
        cartItem1.setQuantity(2);
        testCart.getItems().add(cartItem1);

        CartItem cartItem2 = new CartItem();
        cartItem2.setBook(testBook2);
        cartItem2.setQuantity(1);
        testCart.getItems().add(cartItem2);

        // Create order
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setStatus(OrderStatus.PENDING);
        testOrder.setOrderDate(fixedDateTime);
        testOrder.setItems(new ArrayList<>());
        testOrder.setTotalAmount(new BigDecimal("99.97")); // 2 * 29.99 + 39.99
    }

    @AfterEach
    void tearDown() {
        // Close static mock to avoid "static mocking is already registered" error
        if (dateTimeUtilMock != null) {
            dateTimeUtilMock.close();
        }
    }

    @Test
    void createOrder_withValidCartAndStock_shouldCreateOrderAndUpdateStock() {
        // Arrange
        Long userId = 1L;
        CreateOrderRequest request = new CreateOrderRequest();
        
        when(cartService.getOrCreateCart(userId)).thenReturn(testCart);
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        Order result = orderService.createOrder(userId, request);

        // Assert
        verify(orderRepository).save(orderCaptor.capture());
        verify(bookService).updateStock(testBook1.getId(), -2);
        verify(bookService).updateStock(testBook2.getId(), -1);
        verify(cartService).clearCart(userId);
        
        Order capturedOrder = orderCaptor.getValue();
        assertEquals(OrderStatus.PENDING, capturedOrder.getStatus());
        assertEquals(fixedDateTime, capturedOrder.getOrderDate());
        assertEquals(2, capturedOrder.getItems().size());
        
        BigDecimal expectedTotal = testBook1.getPrice().multiply(new BigDecimal("2"))
                .add(testBook2.getPrice());
        assertEquals(0, expectedTotal.compareTo(capturedOrder.getTotalAmount()));
    }

    @Test
    void createOrder_withEmptyCart_shouldThrowException() {
        // Arrange
        Long userId = 1L;
        CreateOrderRequest request = new CreateOrderRequest();
        testCart.setItems(new ArrayList<>());
        
        when(cartService.getOrCreateCart(userId)).thenReturn(testCart);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> orderService.createOrder(userId, request));
        verify(orderRepository, never()).save(any());
        verify(bookService, never()).updateStock(anyLong(), anyInt());
        verify(cartService, never()).clearCart(any());
    }

    @Test
    void createOrder_withInsufficientStock_shouldThrowException() {
        // Arrange
        Long userId = 1L;
        CreateOrderRequest request = new CreateOrderRequest();
        testBook1.setStockQuantity(1); // Set insufficient stock
        
        when(cartService.getOrCreateCart(userId)).thenReturn(testCart);

        // Act & Assert
        assertThrows(InsufficientStockException.class, () -> orderService.createOrder(userId, request));
        verify(orderRepository, never()).save(any());
        verify(bookService, never()).updateStock(anyLong(), anyInt());
        verify(cartService, never()).clearCart(any());
    }

    @Test
    void getOrderById_withExistingId_shouldReturnOrder() {
        // Arrange
        when(orderRepository.findByIdWithItems(1L)).thenReturn(Optional.of(testOrder));

        // Act
        Order result = orderService.getOrderById(1L);

        // Assert
        assertEquals(testOrder, result);
    }

    @Test
    void getOrderById_withNonExistingId_shouldThrowException() {
        // Arrange
        when(orderRepository.findByIdWithItems(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OrderNotFoundException.class, () -> orderService.getOrderById(99L));
    }

    @Test
    void getUserOrders_shouldReturnPageOfOrders() {
        // Arrange
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        List<Order> orders = Arrays.asList(testOrder);
        Page<Order> orderPage = new PageImpl<>(orders, pageable, orders.size());
        
        when(orderRepository.findByUserIdOrderByOrderDateDesc(userId, pageable)).thenReturn(orderPage);

        // Act
        Page<Order> result = orderService.getUserOrders(userId, pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals(testOrder, result.getContent().get(0));
    }

    @Test
    void updateOrderStatus_shouldUpdateStatusAndSaveOrder() {
        // Arrange
        when(orderRepository.findByIdWithItems(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        Order result = orderService.updateOrderStatus(1L, OrderStatus.PAID);

        // Assert
        verify(orderRepository).save(orderCaptor.capture());
        Order capturedOrder = orderCaptor.getValue();
        assertEquals(OrderStatus.PAID, capturedOrder.getStatus());
    }

    @Test
    void getOrdersByStatus_shouldReturnMatchingOrders() {
        // Arrange
        List<Order> expectedOrders = Arrays.asList(testOrder);
        when(orderRepository.findByStatus(OrderStatus.PENDING)).thenReturn(expectedOrders);

        // Act
        List<Order> result = orderService.getOrdersByStatus(OrderStatus.PENDING);

        // Assert
        assertEquals(expectedOrders, result);
    }

    @Test
    void getOrdersByDateRange_shouldReturnOrdersInRange() {
        // Arrange
        LocalDateTime startDate = fixedDateTime.minusDays(1);
        LocalDateTime endDate = fixedDateTime.plusDays(1);
        List<Order> expectedOrders = Arrays.asList(testOrder);
        
        when(orderRepository.findOrdersInDateRange(startDate, endDate)).thenReturn(expectedOrders);

        // Act
        List<Order> result = orderService.getOrdersByDateRange(startDate, endDate);

        // Assert
        assertEquals(expectedOrders, result);
    }

    @Test
    void cancelOrder_withPendingOrder_shouldCancelOrderAndRestoreStock() {
        // Arrange
        OrderItem item1 = new OrderItem();
        item1.setBook(testBook1);
        item1.setQuantity(2);
        
        OrderItem item2 = new OrderItem();
        item2.setBook(testBook2);
        item2.setQuantity(1);
        
        testOrder.setItems(Arrays.asList(item1, item2));
        
        when(orderRepository.findByIdWithItems(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        orderService.cancelOrder(1L);

        // Assert
        verify(orderRepository).save(orderCaptor.capture());
        verify(bookService).updateStock(testBook1.getId(), 2);
        verify(bookService).updateStock(testBook2.getId(), 1);
        
        Order capturedOrder = orderCaptor.getValue();
        assertEquals(OrderStatus.CANCELLED, capturedOrder.getStatus());
    }

    @Test
    void cancelOrder_withNonPendingOrder_shouldThrowException() {
        // Arrange
        testOrder.setStatus(OrderStatus.PAID);
        when(orderRepository.findByIdWithItems(1L)).thenReturn(Optional.of(testOrder));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> orderService.cancelOrder(1L));
        verify(orderRepository, never()).save(any());
        verify(bookService, never()).updateStock(anyLong(), anyInt());
    }
}