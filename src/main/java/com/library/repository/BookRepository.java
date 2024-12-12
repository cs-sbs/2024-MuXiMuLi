package com.library.repository;

import com.library.domain.book.Book;
import java.util.List;
import java.util.Optional;

public interface BookRepository {
    Book save(Book book);
    Optional<Book> findById(Long id);
    Optional<Book> findByIsbn(String isbn);
    List<Book> findByTitle(String title);
    List<Book> findByType(String type);
    List<Book> findAll();
    void updateStock(Long id, int change);
    void delete(Long id);
    boolean exists(String isbn);
    List<Book> searchBooks(String keyword, String type);
}