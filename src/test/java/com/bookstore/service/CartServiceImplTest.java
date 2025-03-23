package com.bookstore.service;

import com.bookstore.domain.*;
import com.bookstore.exception.BookNotFoundException;
import com.bookstore.exception.CartItemNotFoundException;
import com.bookstore.exception.InsufficientStockException;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.CartRepository;
import com.bookstore.repository.UserRepository;
import com.bookstore.service.impl.CartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private UserRepository userRepository;

    private CartService cartService;
    private User testUser;
    private Book testBook;
    private Cart testCart;

    @BeforeEach
    void setUp() {
        cartService = new CartServiceImpl(cartRepository, bookRepository, userRepository);
        
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("Test Book");
        testBook.setPrice(29.99);
        testBook.setStockQuantity(10);

        testCart = new Cart();
        testCart.setId(1L);
        testCart.setUser(testUser);
        testCart.setItems(new ArrayList<>());
    }

    @Test
    void whenGetOrCreateCart_withExistingCart_thenReturnCart() {
        // Arrange
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));

        // Act
        Cart result = cartService.getOrCreateCart(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testCart.getId(), result.getId());
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void whenAddItemToCart_withNewItem_thenSuccess() {
        // Arrange
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        // Act
        Cart result = cartService.addItemToCart(1L, 1L, 2);

        // Assert
        assertNotNull(result);
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void whenAddItemToCart_withInsufficientStock_thenThrowException() {
        // Arrange
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        // Act & Assert
        assertThrows(InsufficientStockException.class, 
            () -> cartService.addItemToCart(1L, 1L, 15));
    }

    @Test
    void whenRemoveItemFromCart_withExistingItem_thenSuccess() {
        // Arrange
        CartItem item = new CartItem();
        item.setId(1L);
        item.setBook(testBook);
        item.setCart(testCart);
        testCart.getItems().add(item);

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        // Act
        Cart result = cartService.removeItemFromCart(1L, 1L);

        // Assert
        assertNotNull(result);
        assertTrue(result.getItems().isEmpty());
        verify(cartRepository).save(testCart);
    }

    @Test
    void whenRemoveItemFromCart_withNonExistingItem_thenThrowException() {
        // Arrange
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));

        // Act & Assert
        assertThrows(CartItemNotFoundException.class, 
            () -> cartService.removeItemFromCart(1L, 999L));
    }

    @Test
    void whenUpdateCartItemQuantity_withValidQuantity_thenSuccess() {
        // Arrange
        CartItem item = new CartItem();
        item.setId(1L);
        item.setBook(testBook);
        item.setCart(testCart);
        item.setQuantity(1);
        testCart.getItems().add(item);

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        // Act
        Cart result = cartService.updateCartItemQuantity(1L, 1L, 3);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getItems().get(0).getQuantity());
    }
}