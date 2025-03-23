package com.bookstore.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;

@Entity
@Table(name = "cart_items")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Min(1)
    @Column(nullable = false)
    private Integer quantity;

    // Getters
    public Long getId() {
        return id;
    }

    public Cart getCart() {
        return cart;
    }

    public Book getBook() {
        return book;
    }

    public Integer getQuantity() {
        return quantity;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}