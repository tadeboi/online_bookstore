package com.bookstore.controller;

import com.bookstore.domain.Book;
import com.bookstore.domain.Genre;
import com.bookstore.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController extends BaseController {
    private final BookService bookService;

    @GetMapping
    public ResponseEntity<Page<Book>> getAllBooks(Pageable pageable) {
        return ok(bookService.searchBooks("", pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        return ok(bookService.getBookById(id));
    }

    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<Book> getBookByIsbn(@PathVariable String isbn) {
        return ok(bookService.getBookByIsbn(isbn));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Book>> searchBooks(
            @RequestParam String query, 
            Pageable pageable) {
        return ok(bookService.searchBooks(query, pageable));
    }

    @GetMapping("/genre/{genre}")
    public ResponseEntity<Page<Book>> getBooksByGenre(
            @PathVariable Genre genre, 
            Pageable pageable) {
        return ok(bookService.getBooksByGenre(genre, pageable));
    }

    @GetMapping("/author")
    public ResponseEntity<Page<Book>> getBooksByAuthor(
            @RequestParam String author, 
            Pageable pageable) {
        return ok(bookService.getBooksByAuthor(author, pageable));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Book> createBook(@Valid @RequestBody Book book) {
        return ok(bookService.createBook(book));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Book> updateBook(
            @PathVariable Long id, 
            @Valid @RequestBody Book book) {
        return ok(bookService.updateBook(id, book));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/low-stock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Book>> getBooksWithLowStock(
            @RequestParam(defaultValue = "5") int threshold) {
        return ok(bookService.getBooksWithLowStock(threshold));
    }
}