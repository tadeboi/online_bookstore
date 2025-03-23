package com.bookstore.repository;

import com.bookstore.domain.Book;
import com.bookstore.domain.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    // Spring Data JPA automatically implements these methods based on their names
    List<Book> findByTitleContainingIgnoreCase(String title);
    List<Book> findByAuthorContainingIgnoreCase(String author);
    List<Book> findByGenre(Genre genre);
    List<Book> findByPublicationYear(Integer year);
    boolean existsByIsbn(String isbn);
}