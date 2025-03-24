package com.bookstore.service.impl;

import com.bookstore.domain.Book;
import com.bookstore.domain.Cart;
import com.bookstore.domain.CartItem;
import com.bookstore.exception.CartItemNotFoundException;
import com.bookstore.exception.InsufficientStockException;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.CartItemRepository;
import com.bookstore.repository.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    private Long userId;
    private Cart cart;
    private Book book;
    private CartItem cartItem;

    @BeforeEach
    void setUp() {
        userId = 1L;
        
        cart = new Cart();
        cart.setId(1L);
        
        book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setPrice(BigDecimal.valueOf(29.99));
        book.setStockQuantity(10);
        
        cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setCart(cart);
        cartItem.setBook(book);
        cartItem.setQuantity(2);
    }

    @Test
    void getOrCreateCart_whenCartExists_thenReturnCart() {
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));

        Cart result = cartService.getOrCreateCart(userId);

        assertEquals(cart, result);
        verify(cartRepository).findByUserId(userId);
        verifyNoMoreInteractions(cartRepository);
    }

    @Test
    void getOrCreateCart_whenCartDoesNotExist_thenCreateNewCart() {
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        Cart result = cartService.getOrCreateCart(userId);

        assertEquals(cart, result);
        verify(cartRepository).findByUserId(userId);
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void addItemToCart_whenBookHasEnoughStock_thenAddToCart() {
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        when(cartItemRepository.findByCartIdAndBookId(cart.getId(), book.getId())).thenReturn(Optional.empty());
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(cartItem);

        Cart result = cartService.addItemToCart(userId, book.getId(), 2);

        assertEquals(cart, result);
        verify(cartItemRepository).save(any(CartItem.class));
    }

    @Test
    void addItemToCart_whenItemAlreadyInCart_thenUpdateQuantity() {
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        when(cartItemRepository.findByCartIdAndBookId(cart.getId(), book.getId())).thenReturn(Optional.of(cartItem));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(cartItem);

        Cart result = cartService.addItemToCart(userId, book.getId(), 3);

        assertEquals(cart, result);
        verify(cartItemRepository).save(cartItem);
        assertEquals(3, cartItem.getQuantity());
    }

    @Test
    void addItemToCart_whenNotEnoughStock_thenThrowException() {
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));

        assertThrows(InsufficientStockException.class, () -> {
            cartService.addItemToCart(userId, book.getId(), 20);
        });

        verify(cartItemRepository, never()).save(any(CartItem.class));
    }

    @Test
    void updateCartItemQuantity_whenItemExistsAndEnoughStock_thenUpdateQuantity() {
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartIdAndBookId(cart.getId(), book.getId())).thenReturn(Optional.of(cartItem));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(cartItem);

        Cart result = cartService.updateCartItemQuantity(userId, book.getId(), 5);

        assertEquals(cart, result);
        verify(cartItemRepository).save(cartItem);
        assertEquals(5, cartItem.getQuantity());
    }

    @Test
    void updateCartItemQuantity_whenItemNotFound_thenThrowException() {
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartIdAndBookId(cart.getId(), book.getId())).thenReturn(Optional.empty());

        assertThrows(CartItemNotFoundException.class, () -> {
            cartService.updateCartItemQuantity(userId, book.getId(), 5);
        });

        verify(cartItemRepository, never()).save(any(CartItem.class));
    }

    @Test
    void updateCartItemQuantity_whenNotEnoughStock_thenThrowException() {
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartIdAndBookId(cart.getId(), book.getId())).thenReturn(Optional.of(cartItem));

        assertThrows(InsufficientStockException.class, () -> {
            cartService.updateCartItemQuantity(userId, book.getId(), 20);
        });

        verify(cartItemRepository, never()).save(any(CartItem.class));
    }

    @Test
    void removeItemFromCart_whenItemExists_thenRemoveItem() {
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartIdAndBookId(cart.getId(), book.getId())).thenReturn(Optional.of(cartItem));
        doNothing().when(cartItemRepository).delete(cartItem);

        Cart result = cartService.removeItemFromCart(userId, book.getId());

        assertEquals(cart, result);
        verify(cartItemRepository).delete(cartItem);
    }

    @Test
    void removeItemFromCart_whenItemNotFound_thenThrowException() {
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartIdAndBookId(cart.getId(), book.getId())).thenReturn(Optional.empty());

        assertThrows(CartItemNotFoundException.class, () -> {
            cartService.removeItemFromCart(userId, book.getId());
        });

        verify(cartItemRepository, never()).delete(any(CartItem.class));
    }

    @Test
    void clearCart_shouldDeleteAllCartItems() {
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        doNothing().when(cartItemRepository).deleteByCartId(cart.getId());

        cartService.clearCart(userId);

        verify(cartItemRepository).deleteByCartId(cart.getId());
    }
}