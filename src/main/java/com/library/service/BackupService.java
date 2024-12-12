package com.library.service;

import com.library.domain.book.Book;
import org.apache.log4j.Logger;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.*;

public class BackupService {
    private static final Logger logger = Logger.getLogger(BackupService.class);
    private final BookService bookService;
    private final FileStorageService fileStorageService;
    private final ScheduledExecutorService scheduler;
    private final ExecutorService backupExecutor;
    private final String backupDir;

    public BackupService(BookService bookService, FileStorageService fileStorageService, String backupDir) {
        this.bookService = bookService;
        this.fileStorageService = fileStorageService;
        this.backupDir = backupDir;
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.backupExecutor = Executors.newSingleThreadExecutor();
        
        // 创建备份目录
        new File(backupDir).mkdirs();
    }

    public void startScheduledBackup(int interval, TimeUnit unit) {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                performBackup();
            } catch (Exception e) {
                logger.error("Scheduled backup failed", e);
            }
        }, 0, interval, unit);
        logger.info("Scheduled backup started with interval: " + interval + " " + unit);
    }

    public Future<String> performBackup() {
        return backupExecutor.submit(() -> {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = "books_backup_" + timestamp + ".json";
            
            List<Book> books = bookService.findAll();
            fileStorageService.saveBooks(books, filename).get();
            
            logger.debug("Backup completed: " + filename);
            return filename;
        });
    }

    public Future<Integer> restoreFromBackup(String filename) {
        return backupExecutor.submit(() -> {
            try {
                List<Book> books = fileStorageService.loadBooks(filename).get();
                int restoredCount = 0;
                
                for (Book book : books) {
                    try {
                        bookService.addBook(book);
                        restoredCount++;
                    } catch (Exception e) {
                        logger.error("Failed to restore book: " + book.getIsbn(), e);
                    }
                }
                
                logger.info("Restored " + restoredCount + " books from backup: " + filename);
                return restoredCount;
            } catch (Exception e) {
                logger.error("Failed to restore from backup: " + filename, e);
                throw e;
            }
        });
    }

    public List<String> listBackups() {
        File dir = new File(backupDir);
        String[] files = dir.list((d, name) -> name.startsWith("books_backup_") && name.endsWith(".json"));
        return files != null ? List.of(files) : List.of();
    }

    public void shutdown() {
        try {
            scheduler.shutdown();
            backupExecutor.shutdown();
            
            if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
            if (!backupExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
                backupExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            backupExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
} 