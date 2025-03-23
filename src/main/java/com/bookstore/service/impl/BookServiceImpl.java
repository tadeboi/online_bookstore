package com.bookstore.service.impl;

import com.bookstore.domain.Book;
import com.bookstore.domain.Genre;
import com.bookstore.exception.BookNotFoundException;
import com.bookstore.exception.InvalidBookDataException;
import com.bookstore.repository.BookRepository;
import com.bookstore.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;

    @Override
    @Transactional
    public Book saveBook(Book book) {
        validateBook(book);
        if (bookRepository.existsByIsbn(book.getIsbn())) {
            throw new InvalidBookDataException("Book with ISBN " + book.getIsbn() + " already exists");
        }
        return bookRepository.save(book);
    }

    @Override
    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }

    @Override
    public List<Book> findByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }

    @Override
    public List<Book> findByAuthor(String author) {
        return bookRepository.findByAuthorContainingIgnoreCase(author);
    }

    @Override
    public List<Book> findByGenre(Genre genre) {
        return bookRepository.findByGenre(genre);
    }

    @Override
    public List<Book> findByPublicationYear(Integer year) {
        return bookRepository.findByPublicationYear(year);
    }

    @Override
    @Transactional
    public void updateStock(Long bookId, Integer quantity) {
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + bookId));
        book.setStockQuantity(book.getStockQuantity() + quantity);
        bookRepository.save(book);
    }

    @Override
    @Transactional
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new BookNotFoundException("Book not found with id: " + id);
        }
        bookRepository.deleteById(id);
    }

    private void validateBook(Book book) {
        if (book.getTitle() == null || !book.getTitle().matches("^[a-zA-Z0-9\\s]+$")) {
            throw new InvalidBookDataException("Invalid book title");
        }
        if (book.getIsbn() == null || !book.getIsbn().matches("^[0-9-]+$")) {
            throw new InvalidBookDataException("Invalid ISBN format");
        }
        if (book.getPrice() == null || book.getPrice() <= 0) {
            throw new InvalidBookDataException("Invalid price");
        }
        if (book.getStockQuantity() == null || book.getStockQuantity() < 0) {
            throw new InvalidBookDataException("Invalid stock quantity");
        }
    }
}