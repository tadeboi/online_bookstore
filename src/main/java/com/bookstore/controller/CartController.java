package com.bookstore.controller;

import com.bookstore.domain.Cart;
import com.bookstore.dto.AddToCartRequest;
import com.bookstore.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController extends BaseController {
    private final CartService cartService;

    @GetMapping
    public ResponseEntity<Cart> getCart() {
        return ok(cartService.getOrCreateCart(getCurrentUserId()));
    }

    @PostMapping("/items")
    public ResponseEntity<Cart> addToCart(@Valid @RequestBody AddToCartRequest request) {
        return ok(cartService.addItemToCart(
            getCurrentUserId(), 
            request.getBookId(), 
            request.getQuantity()
        ));
    }

    @PutMapping("/items/{bookId}")
    public ResponseEntity<Cart> updateCartItem(
            @PathVariable Long bookId,
            @RequestParam int quantity) {
        return ok(cartService.updateCartItemQuantity(
            getCurrentUserId(), 
            bookId, 
            quantity
        ));
    }

    @DeleteMapping("/items/{bookId}")
    public ResponseEntity<Cart> removeFromCart(@PathVariable Long bookId) {
        return ok(cartService.removeItemFromCart(getCurrentUserId(), bookId));
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart() {
        cartService.clearCart(getCurrentUserId());
        return ResponseEntity.noContent().build();
    }
}