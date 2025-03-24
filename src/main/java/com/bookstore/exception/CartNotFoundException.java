package com.bookstore.exception;

public class CartNotFoundException extends RuntimeException {
    public CartNotFoundException(String message) {
        super(message);
    }

    public CartNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CartNotFoundException(Long cartId) {
        super("Cart not found with id: " + cartId);
    }

    public CartNotFoundException(String username, String message) {
        super("Cart not found for user " + username + ": " + message);
    }
}