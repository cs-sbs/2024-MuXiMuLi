package com.library.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.library.domain.book.Book;
import org.apache.log4j.Logger;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class FileStorageService {
    private static final Logger logger = Logger.getLogger(FileStorageService.class);
    private final String storageDir;
    private final ObjectMapper objectMapper;

    public FileStorageService(String storageDir) {
        this.storageDir = storageDir;
        this.objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());
        
        try {
            Files.createDirectories(Paths.get(storageDir));
            logger.info("File storage initialized at: " + storageDir);
        } catch (Exception e) {
            logger.error("Failed to create storage directory: " + storageDir, e);
            throw new RuntimeException("Failed to initialize file storage", e);
        }
    }

    public CompletableFuture<Void> saveBooks(List<Book> books, String filename) {
        return CompletableFuture.runAsync(() -> {
            try {
                Path filePath = Paths.get(storageDir, filename);
                objectMapper.writeValue(filePath.toFile(), books);
                logger.debug("Successfully saved books to file: " + filePath);
            } catch (Exception e) {
                logger.error("Failed to save books to file: " + filename, e);
                throw new RuntimeException("Failed to save books", e);
            }
        });
    }

    public CompletableFuture<List<Book>> loadBooks(String filename) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Path filePath = Paths.get(storageDir, filename);
                List<Book> books = objectMapper.readValue(
                    filePath.toFile(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Book.class)
                );
                logger.debug("Successfully loaded " + books.size() + " books from file: " + filePath);
                return books;
            } catch (Exception e) {
                logger.error("Failed to load books from file: " + filename, e);
                throw new RuntimeException("Failed to load books", e);
            }
        });
    }

    public void deleteFile(String filename) {
        try {
            Path filePath = Paths.get(storageDir, filename);
            Files.deleteIfExists(filePath);
            logger.debug("Successfully deleted file: " + filePath);
        } catch (Exception e) {
            logger.error("Failed to delete file: " + filename, e);
            throw new RuntimeException("Failed to delete file", e);
        }
    }
} 