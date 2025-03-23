package com.bookstore.service;

import com.bookstore.domain.Cart;
import com.bookstore.domain.CartItem;

public interface CartService {
    Cart getOrCreateCart(Long userId);
    Cart addItemToCart(Long userId, Long bookId, Integer quantity);
    Cart removeItemFromCart(Long userId, Long bookId);
    Cart updateCartItemQuantity(Long userId, Long bookId, Integer quantity);
    void clearCart(Long userId);
}