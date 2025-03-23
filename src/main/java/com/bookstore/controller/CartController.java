package com.bookstore.controller;

import com.bookstore.domain.Cart;
import com.bookstore.dto.AddToCartRequest;
import com.bookstore.service.CartService;
import com.bookstore.security.CurrentUser;
import com.bookstore.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping
    public ResponseEntity<Cart> viewCart(@CurrentUser UserPrincipal currentUser) {
        return ResponseEntity.ok(cartService.getOrCreateCart(currentUser.getId()));
    }

    @PostMapping("/items")
    public ResponseEntity<Cart> addItemToCart(
            @CurrentUser UserPrincipal currentUser,
            @Valid @RequestBody AddToCartRequest request) {
        return ResponseEntity.ok(cartService.addItemToCart(
            currentUser.getId(), 
            request.getBookId(), 
            request.getQuantity()
        ));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Cart> removeItemFromCart(
            @CurrentUser UserPrincipal currentUser,
            @PathVariable Long itemId) {
        return ResponseEntity.ok(cartService.removeItemFromCart(currentUser.getId(), itemId));
    }
}