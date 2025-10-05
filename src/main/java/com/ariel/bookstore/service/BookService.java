package com.ariel.bookstore.service;

import com.ariel.bookstore.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.UUID;

public interface BookService {

    Book create(Book book);
    Book getById(UUID id);
    Page<Book> list(Pageable pageable);
    Page<Book> search(String query, Pageable pageable);
    Page<Book> findByPriceBetween(BigDecimal min, BigDecimal max, Pageable pageable);
    Book update(UUID id, Book changes);
    void delete(UUID id);
    void deleteAll();
}
