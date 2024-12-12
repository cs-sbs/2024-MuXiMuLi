package com.library.service;

import com.library.domain.book.Book;
import java.util.List;
import java.util.Optional;

public interface BookService {
    Book addBook(Book book);
    Optional<Book> findById(Long id);
    Optional<Book> findByIsbn(String isbn);
    List<Book> findByTitle(String title);
    List<Book> findByType(String type);
    List<Book> findAll();
    void updateStock(Long id, int change);
    void deleteBook(Long id);
    List<Book> searchBooks(String keyword, String type);
} 