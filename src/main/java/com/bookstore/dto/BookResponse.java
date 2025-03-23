package com.bookstore.dto;

import com.bookstore.domain.Genre;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class BookResponse {
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private Genre genre;
    private Integer publicationYear;
    private BigDecimal price;
    private Integer stockQuantity;
    private String description;
}