package com.bookstore.service;

import com.bookstore.domain.Book;
import java.util.List;
import java.util.Optional;

public interface BookService {
    Book saveBook(Book book);
    Optional<Book> findById(Long id);
    List<Book> findByTitle(String title);
    List<Book> findByAuthor(String author);
    List<Book> findByGenre(Genre genre);
    List<Book> findByPublicationYear(Integer year);
    void updateStock(Long bookId, Integer quantity);
    void deleteBook(Long id);
}