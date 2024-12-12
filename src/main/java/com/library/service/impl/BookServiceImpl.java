package com.library.service.impl;

import com.library.domain.book.Book;
import com.library.repository.BookRepository;
import com.library.service.BookService;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Optional;

public class BookServiceImpl implements BookService {
    private static final Logger logger = Logger.getLogger(BookServiceImpl.class);
    private final BookRepository bookRepository;
    
    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }
    
    @Override
    public Book addBook(Book book) {
        try {
            // 验证图书信息
            book.validate();
            
            // 检查ISBN是否已存在
            if (bookRepository.findByIsbn(book.getIsbn()).isPresent()) {
                throw new IllegalArgumentException("ISBN已存在");
            }
            
            // 保存图书
            return bookRepository.save(book);
        } catch (Exception e) {
            logger.error("Error adding book: " + book.getIsbn(), e);
            throw new RuntimeException("添加图书失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Optional<Book> findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        return bookRepository.findById(id);
    }
    
    @Override
    public Optional<Book> findByIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN cannot be empty");
        }
        return bookRepository.findByIsbn(isbn);
    }
    
    @Override
    public List<Book> findByTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        return bookRepository.findByTitle(title);
    }
    
    @Override
    public List<Book> findByType(String type) {
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Type cannot be empty");
        }
        return bookRepository.findByType(type);
    }
    
    @Override
    public List<Book> findAll() {
        return bookRepository.findAll();
    }
    
    @Override
    public void updateStock(Long id, int newStock) {
        if (newStock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }
        
        // 直接调用 repository 的 updateStock 方法
        try {
            bookRepository.updateStock(id, newStock);
        } catch (Exception e) {
            logger.error("Error updating stock for book id: " + id, e);
            throw new RuntimeException("更新库存失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void deleteBook(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        bookRepository.delete(id);
    }
    
    @Override
    public List<Book> searchBooks(String keyword, String type) {
        try {
            return bookRepository.searchBooks(keyword, type);
        } catch (Exception e) {
            logger.error("Error searching books", e);
            throw new RuntimeException("搜索图书失败: " + e.getMessage(), e);
        }
    }
} 