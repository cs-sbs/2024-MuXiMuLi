package com.library;

import com.library.service.BackupService;
import com.library.service.BookService;
import com.library.service.UserService;

public class ApplicationContext {
    private static BookService bookService;
    private static UserService userService;
    private static BackupService backupService;
    
    public static void init(BookService bookService, UserService userService, BackupService backupService) {
        ApplicationContext.bookService = bookService;
        ApplicationContext.userService = userService;
        ApplicationContext.backupService = backupService;
    }
    
    public static BookService getBookService() {
        return bookService;
    }
    
    public static UserService getUserService() {
        return userService;
    }
    
    public static BackupService getBackupService() {
        return backupService;
    }
} 