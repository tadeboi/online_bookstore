package com.bookstore.service;

import com.bookstore.domain.Cart;

public interface CartService {
    Cart getOrCreateCart(Long userId);
    Cart addItemToCart(Long userId, Long bookId, int quantity);
    Cart updateCartItemQuantity(Long userId, Long bookId, int quantity);
    Cart removeItemFromCart(Long userId, Long bookId);
    void clearCart(Long userId);
}