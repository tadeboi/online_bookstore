package com.bookstore.service.impl;

import com.bookstore.domain.Book;
import com.bookstore.domain.Cart;
import com.bookstore.domain.CartItem;
import com.bookstore.domain.User;
import com.bookstore.exception.BookNotFoundException;
import com.bookstore.exception.CartItemNotFoundException;
import com.bookstore.exception.InsufficientStockException;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.CartRepository;
import com.bookstore.repository.UserRepository;
import com.bookstore.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
            .orElseGet(() -> {
                User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalStateException("User not found"));
                Cart newCart = new Cart();
                newCart.setUser(user);
                return cartRepository.save(newCart);
            });
    }

    @Override
    @Transactional
    public Cart addItemToCart(Long userId, Long bookId, Integer quantity) {
        Cart cart = getOrCreateCart(userId);
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new BookNotFoundException("Book not found"));

        if (book.getStockQuantity() < quantity) {
            throw new InsufficientStockException("Not enough stock available");
        }

        CartItem existingItem = cart.getItems().stream()
            .filter(item -> item.getBook().getId().equals(bookId))
            .findFirst()
            .orElse(null);

        if (existingItem != null) {
            if (book.getStockQuantity() < existingItem.getQuantity() + quantity) {
                throw new InsufficientStockException("Not enough stock available");
            }
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setBook(book);
            newItem.setQuantity(quantity);
            cart.getItems().add(newItem);
        }

        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public Cart removeItemFromCart(Long userId, Long itemId) {
        Cart cart = getOrCreateCart(userId);
        
        boolean removed = cart.getItems().removeIf(item -> item.getId().equals(itemId));
        if (!removed) {
            throw new CartItemNotFoundException("Cart item not found");
        }

        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public Cart updateCartItemQuantity(Long userId, Long bookId, Integer quantity) {
        Cart cart = getOrCreateCart(userId);
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new BookNotFoundException("Book not found"));

        if (book.getStockQuantity() < quantity) {
            throw new InsufficientStockException("Not enough stock available");
        }

        CartItem item = cart.getItems().stream()
            .filter(i -> i.getBook().getId().equals(bookId))
            .findFirst()
            .orElseThrow(() -> new CartItemNotFoundException("Cart item not found"));

        item.setQuantity(quantity);
        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        cart.getItems().clear();
        cartRepository.save(cart);
    }
}