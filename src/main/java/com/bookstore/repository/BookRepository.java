package com.bookstore.repository;

import com.bookstore.domain.Book;
import com.bookstore.domain.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByIsbn(String isbn);
    
    Page<Book> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    
    Page<Book> findByAuthorContainingIgnoreCase(String author, Pageable pageable);
    
    Page<Book> findByGenre(Genre genre, Pageable pageable);
    
    @Query("SELECT b FROM Book b WHERE b.stockQuantity > 0")
    Page<Book> findAvailableBooks(Pageable pageable);
    
    boolean existsByIsbn(String isbn);
    
    @Query("SELECT b FROM Book b WHERE b.stockQuantity < :threshold")
    List<Book> findBooksWithLowStock(int threshold);
}