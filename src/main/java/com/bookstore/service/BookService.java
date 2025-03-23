package com.bookstore.service;

import com.bookstore.domain.Book;
import com.bookstore.domain.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookService {
    Book createBook(Book book);
    Book updateBook(Long id, Book book);
    void deleteBook(Long id);
    Book getBookById(Long id);
    Book getBookByIsbn(String isbn);
    Page<Book> searchBooks(String query, Pageable pageable);
    Page<Book> getBooksByGenre(Genre genre, Pageable pageable);
    Page<Book> getBooksByAuthor(String author, Pageable pageable);
    List<Book> getBooksWithLowStock(int threshold);
    void updateStock(Long bookId, int quantity);
}