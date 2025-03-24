package com.bookstore.service.impl;

import com.bookstore.domain.Book;
import com.bookstore.domain.Genre;
import com.bookstore.exception.BookNotFoundException;
import com.bookstore.exception.InvalidBookDataException;
import com.bookstore.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    @Captor
    private ArgumentCaptor<Book> bookCaptor;

    private Book validBook;
    private Book invalidBook;
    private List<Book> bookList;
    private Page<Book> bookPage;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        validBook = new Book();
        validBook.setId(1L);
        validBook.setTitle("Test Book");
        validBook.setAuthor("Test Author");
        validBook.setIsbn("9781234567890");
        validBook.setPrice(BigDecimal.valueOf(29.99));
        validBook.setStockQuantity(10);
        validBook.setGenre(Genre.FICTION);
        validBook.setDescription("A test book description");

        invalidBook = new Book();
        
        bookList = Arrays.asList(validBook);
        bookPage = new PageImpl<>(bookList);
        pageable = PageRequest.of(0, 10);
    }

    @Test
    void createBook_whenValidBook_thenBookCreated() {
        when(bookRepository.existsByIsbn(anyString())).thenReturn(false);
        when(bookRepository.save(any(Book.class))).thenReturn(validBook);

        Book result = bookService.createBook(validBook);

        assertEquals(validBook, result);
        verify(bookRepository).existsByIsbn(validBook.getIsbn());
        verify(bookRepository).save(validBook);
    }

    @Test
    void createBook_whenIsbnExists_thenThrowException() {
        when(bookRepository.existsByIsbn(anyString())).thenReturn(true);

        assertThrows(InvalidBookDataException.class, () -> {
            bookService.createBook(validBook);
        });

        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void createBook_whenInvalidBook_thenThrowException() {
        assertThrows(InvalidBookDataException.class, () -> {
            bookService.createBook(invalidBook);
        });

        verify(bookRepository, never()).existsByIsbn(anyString());
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void updateBook_whenValidBookAndIdExists_thenBookUpdated() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(validBook));
        when(bookRepository.save(any(Book.class))).thenReturn(validBook);

        Book bookDetails = new Book();
        bookDetails.setTitle("Updated Title");
        bookDetails.setAuthor("Updated Author");
        bookDetails.setIsbn("9781234567890");
        bookDetails.setPrice(BigDecimal.valueOf(39.99));
        bookDetails.setStockQuantity(15);
        bookDetails.setGenre(Genre.FICTION);
        bookDetails.setDescription("Updated description");

        Book result = bookService.updateBook(1L, bookDetails);

        verify(bookRepository).save(bookCaptor.capture());
        Book capturedBook = bookCaptor.getValue();
        
        assertEquals("Updated Title", capturedBook.getTitle());
        assertEquals("Updated Author", capturedBook.getAuthor());
        assertEquals(Genre.FICTION, capturedBook.getGenre());
        assertEquals(BigDecimal.valueOf(39.99), capturedBook.getPrice());
        assertEquals(15, capturedBook.getStockQuantity());
        assertEquals("Updated description", capturedBook.getDescription());
    }

    @Test
    void updateBook_whenInvalidBook_thenThrowException() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(validBook));

        assertThrows(InvalidBookDataException.class, () -> {
            bookService.updateBook(1L, invalidBook);
        });

        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void updateBook_whenBookNotFound_thenThrowException() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> {
            bookService.updateBook(1L, validBook);
        });

        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void deleteBook_whenBookExists_thenBookDeleted() {
        when(bookRepository.existsById(anyLong())).thenReturn(true);
        doNothing().when(bookRepository).deleteById(anyLong());

        bookService.deleteBook(1L);

        verify(bookRepository).deleteById(1L);
    }

    @Test
    void deleteBook_whenBookNotFound_thenThrowException() {
        when(bookRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(BookNotFoundException.class, () -> {
            bookService.deleteBook(1L);
        });

        verify(bookRepository, never()).deleteById(anyLong());
    }

    @Test
    void getBookById_whenBookExists_thenReturnBook() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(validBook));

        Book result = bookService.getBookById(1L);

        assertEquals(validBook, result);
    }

    @Test
    void getBookById_whenBookNotFound_thenThrowException() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> {
            bookService.getBookById(1L);
        });
    }

    @Test
    void getBookByIsbn_whenBookExists_thenReturnBook() {
        when(bookRepository.findByIsbn(anyString())).thenReturn(Optional.of(validBook));

        Book result = bookService.getBookByIsbn("9781234567890");

        assertEquals(validBook, result);
    }

    @Test
    void getBookByIsbn_whenBookNotFound_thenThrowException() {
        when(bookRepository.findByIsbn(anyString())).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> {
            bookService.getBookByIsbn("9781234567890");
        });
    }

    @Test
    void searchBooks_shouldReturnPageOfBooks() {
        when(bookRepository.findByTitleContainingIgnoreCase(anyString(), any(Pageable.class)))
            .thenReturn(bookPage);

        Page<Book> result = bookService.searchBooks("Test", pageable);

        assertEquals(bookPage, result);
    }

    @Test
    void getBooksByGenre_shouldReturnPageOfBooks() {
        when(bookRepository.findByGenre(any(Genre.class), any(Pageable.class)))
            .thenReturn(bookPage);

        Page<Book> result = bookService.getBooksByGenre(Genre.FICTION, pageable);

        assertEquals(bookPage, result);
    }

    @Test
    void getBooksByAuthor_shouldReturnPageOfBooks() {
        when(bookRepository.findByAuthorContainingIgnoreCase(anyString(), any(Pageable.class)))
            .thenReturn(bookPage);

        Page<Book> result = bookService.getBooksByAuthor("Test", pageable);

        assertEquals(bookPage, result);
    }

    @Test
    void getBooksWithLowStock_shouldReturnListOfBooks() {
        when(bookRepository.findBooksWithLowStock(anyInt())).thenReturn(bookList);

        List<Book> result = bookService.getBooksWithLowStock(5);

        assertEquals(bookList, result);
    }

    @Test
    void updateStock_whenAddingStock_thenIncreaseStockQuantity() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(validBook));
        when(bookRepository.save(any(Book.class))).thenReturn(validBook);

        bookService.updateStock(1L, 5);

        verify(bookRepository).save(bookCaptor.capture());
        Book capturedBook = bookCaptor.getValue();
        
        assertEquals(15, capturedBook.getStockQuantity());
    }

    @Test
    void updateStock_whenReducingStock_thenDecreaseStockQuantity() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(validBook));
        when(bookRepository.save(any(Book.class))).thenReturn(validBook);

        bookService.updateStock(1L, -5);

        verify(bookRepository).save(bookCaptor.capture());
        Book capturedBook = bookCaptor.getValue();
        
        assertEquals(5, capturedBook.getStockQuantity());
    }

    @Test
    void updateStock_whenReducingBelowZero_thenThrowException() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(validBook));

        assertThrows(InvalidBookDataException.class, () -> {
            bookService.updateStock(1L, -15);
        });

        verify(bookRepository, never()).save(any(Book.class));
    }
}