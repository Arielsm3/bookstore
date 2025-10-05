package com.ariel.bookstore.service.impl;

import com.ariel.bookstore.model.Book;
import com.ariel.bookstore.repository.BookRepository;
import com.ariel.bookstore.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class BookServiceImpl implements BookService {

    private final BookRepository repository;

    @Override
    public Book create(Book book) {
        return repository.save(book);
    }

    @Override
    public Book getById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new NoSuchElementException("Book not found."));
    }

    @Override
    public Page<Book> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public Page<Book> search(String query, Pageable pageable) {
        // Search either title or author; Combine by title first, fallback to author
        Page<Book> byTitle = repository.findByTitleContainingIgnoreCase(query, pageable);
        Page<Book> byAuthor = repository.findByAuthorContainingIgnoreCase(query, pageable);

        return byTitle.hasContent() ? byTitle : byAuthor;
    }

    @Override
    public Page<Book> findByPriceBetween(BigDecimal min, BigDecimal max, Pageable pageable) {
        return repository.findByPriceBetween(min, max, pageable);
    }

    @Override
    public Book update(UUID id, Book changes) {
        Book current = getById(id);

        current.setTitle(changes.getTitle());
        current.setAuthor(changes.getAuthor());
        current.setIsbn(changes.getIsbn());
        current.setPrice(changes.getPrice());
        current.setStock(changes.getStock());

        return repository.save(current);
    }

    @Override
    public void delete(UUID id) {
        if(!repository.existsById(id)) throw new NoSuchElementException("Book not found!");
        repository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }
}
