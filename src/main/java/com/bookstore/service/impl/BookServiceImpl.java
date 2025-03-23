package com.bookstore.service.impl;

import com.bookstore.domain.Book;
import com.bookstore.domain.Genre;
import com.bookstore.exception.BookNotFoundException;
import com.bookstore.exception.InvalidBookDataException;
import com.bookstore.repository.BookRepository;
import com.bookstore.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;

    @Override
    @Transactional
    public Book createBook(Book book) {
        validateBook(book);
        if (bookRepository.existsByIsbn(book.getIsbn())) {
            throw new InvalidBookDataException("Book with ISBN " + book.getIsbn() + " already exists");
        }
        return bookRepository.save(book);
    }

    @Override
    @Transactional
    public Book updateBook(Long id, Book bookDetails) {
        Book book = getBookById(id);
        validateBook(bookDetails);
        
        book.setTitle(bookDetails.getTitle());
        book.setAuthor(bookDetails.getAuthor());
        book.setGenre(bookDetails.getGenre());
        book.setPrice(bookDetails.getPrice());
        book.setStockQuantity(bookDetails.getStockQuantity());
        book.setDescription(bookDetails.getDescription());
        
        return bookRepository.save(book);
    }

    @Override
    @Transactional
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new BookNotFoundException("Book not found with id: " + id);
        }
        bookRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Book getBookById(Long id) {
        return bookRepository.findById(id)
            .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Book getBookByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn)
            .orElseThrow(() -> new BookNotFoundException("Book not found with ISBN: " + isbn));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Book> searchBooks(String query, Pageable pageable) {
        return bookRepository.findByTitleContainingIgnoreCase(query, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Book> getBooksByGenre(Genre genre, Pageable pageable) {
        return bookRepository.findByGenre(genre, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Book> getBooksByAuthor(String author, Pageable pageable) {
        return bookRepository.findByAuthorContainingIgnoreCase(author, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> getBooksWithLowStock(int threshold) {
        return bookRepository.findBooksWithLowStock(threshold);
    }

    @Override
    @Transactional
    public void updateStock(Long bookId, int quantity) {
        Book book = getBookById(bookId);
        int newQuantity = book.getStockQuantity() + quantity;
        if (newQuantity < 0) {
            throw new InvalidBookDataException("Cannot reduce stock below 0");
        }
        book.setStockQuantity(newQuantity);
        bookRepository.save(book);
    }

    private void validateBook(Book book) {
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new InvalidBookDataException("Book title cannot be empty");
        }
        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            throw new InvalidBookDataException("Book author cannot be empty");
        }
        if (book.getPrice() == null || book.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidBookDataException("Book price must be greater than 0");
        }
        if (book.getStockQuantity() == null || book.getStockQuantity() < 0) {
            throw new InvalidBookDataException("Book stock quantity cannot be negative");
        }
        if (book.getIsbn() == null || !book.getIsbn().matches("^\\d{10}|\\d{13}$")) {
            throw new InvalidBookDataException("Invalid ISBN format");
        }
    }
}