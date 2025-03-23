package com.bookstore.service.impl;

import com.bookstore.domain.Book;
import com.bookstore.domain.Cart;
import com.bookstore.domain.CartItem;
import com.bookstore.exception.CartItemNotFoundException;
import com.bookstore.exception.InsufficientStockException;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.CartItemRepository;
import com.bookstore.repository.CartRepository;
import com.bookstore.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;

    @Override
    @Transactional
    public Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
            .orElseGet(() -> {
                Cart cart = new Cart();
                // User should be set here
                return cartRepository.save(cart);
            });
    }

    @Override
    @Transactional
    public Cart addItemToCart(Long userId, Long bookId, int quantity) {
        Cart cart = getOrCreateCart(userId);
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new RuntimeException("Book not found"));

        if (book.getStockQuantity() < quantity) {
            throw new InsufficientStockException("Not enough stock available");
        }

        CartItem cartItem = cartItemRepository
            .findByCartIdAndBookId(cart.getId(), bookId)
            .orElseGet(() -> {
                CartItem newItem = new CartItem();
                newItem.setCart(cart);
                newItem.setBook(book);
                return newItem;
            });

        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
        return cart;
    }

    @Override
    @Transactional
    public Cart updateCartItemQuantity(Long userId, Long bookId, int quantity) {
        Cart cart = getOrCreateCart(userId);
        CartItem cartItem = cartItemRepository
            .findByCartIdAndBookId(cart.getId(), bookId)
            .orElseThrow(() -> new CartItemNotFoundException("Cart item not found"));

        if (cartItem.getBook().getStockQuantity() < quantity) {
            throw new InsufficientStockException("Not enough stock available");
        }

        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
        return cart;
    }

    @Override
    @Transactional
    public Cart removeItemFromCart(Long userId, Long bookId) {
        Cart cart = getOrCreateCart(userId);
        CartItem cartItem = cartItemRepository
            .findByCartIdAndBookId(cart.getId(), bookId)
            .orElseThrow(() -> new CartItemNotFoundException("Cart item not found"));

        cartItemRepository.delete(cartItem);
        return cart;
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        cartItemRepository.deleteByCartId(cart.getId());
    }
}