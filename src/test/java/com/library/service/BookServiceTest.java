package com.library.service;

import com.library.domain.book.Book;
import com.library.domain.book.ComputerBook;
import com.library.domain.book.LiteratureBook;
import com.library.repository.BookRepository;
import com.library.repository.impl.BookRepositoryImpl;
import com.library.service.impl.BookServiceImpl;
import com.library.util.DBHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class BookServiceTest {
    private BookService bookService;
    private BookRepository bookRepository;
    
    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        bookRepository = new BookRepositoryImpl();
        bookService = new BookServiceImpl(bookRepository);
    }

    @Test
    void testAddAndFindComputerBook() {
        // 创建计算机类图书
        ComputerBook book = new ComputerBook();
        book.setIsbn("TEST-" + System.currentTimeMillis());
        book.setTitle("Java编程思想");
        book.setAuthor("Bruce Eckel");
        book.setType("Computer");
        book.setStock(10);
        book.setProgrammingLanguage("Java");
        book.setFramework("Spring");
        book.setDifficulty("Intermediate");

        // 保存图书
        Book savedBook = bookService.addBook(book);
        assertNotNull(savedBook, "图书添加应该成功");
        assertNotNull(savedBook.getId(), "保存的图书应该有ID");

        // 查找并验证
        Optional<Book> found = bookService.findById(savedBook.getId());
        assertTrue(found.isPresent(), "应该能找到保存的图书");
        assertEquals(book.getIsbn(), found.get().getIsbn(), "ISBN应该匹配");
    }

    @Test
    void testAddAndFindLiteratureBook() {
        // 创建文学类图书
        LiteratureBook book = new LiteratureBook();
        book.setIsbn("TEST-" + System.currentTimeMillis());
        book.setTitle("红楼梦");
        book.setAuthor("曹雪芹");
        book.setType("Literature");
        book.setStock(5);
        book.setGenre("Classical");
        book.setEra("Qing Dynasty");
        book.setLanguage("Chinese");

        // 保存图书
        Book savedBook = bookService.addBook(book);
        assertNotNull(savedBook, "图书添加应该成功");
        assertNotNull(savedBook.getId(), "保存的图书应该有ID");

        // 查找并验证
        Optional<Book> found = bookService.findById(savedBook.getId());
        assertTrue(found.isPresent(), "应该能找到保存的图书");
        assertEquals(book.getIsbn(), found.get().getIsbn(), "ISBN应该匹配");
    }

    @Test
    void testFindByType() {
        // 添加一本计算机类图书
        ComputerBook book = new ComputerBook();
        book.setIsbn("TEST-" + System.currentTimeMillis());
        book.setTitle("Java编程思想");
        book.setAuthor("Bruce Eckel");
        book.setType("Computer");
        book.setStock(10);
        // 添加必要的计算机类图书属性
        book.setProgrammingLanguage("Java");
        book.setFramework("Spring");
        book.setDifficulty("Intermediate");

        Book savedBook = bookService.addBook(book);
        assertNotNull(savedBook, "图书添加应该成功");

        // 测试查找
        List<Book> books = bookService.findByType("Computer");
        assertFalse(books.isEmpty(), "应该能找到计算机类图书");
        assertTrue(books.stream().anyMatch(b -> b.getIsbn().equals(book.getIsbn())));
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testUpdateStock() {
        // 清理测试数据
        try (Connection conn = DBHandler.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM books");
            conn.commit();  // 确保清理操作被提交
        } catch (SQLException e) {
            fail("Failed to clean test data: " + e.getMessage());
        }

        // 准备测试数据
        ComputerBook book = new ComputerBook();
        book.setTitle("测试图书");
        book.setAuthor("测试作者");
        book.setIsbn("TEST-" + System.currentTimeMillis());
        book.setType("Computer");
        book.setStock(10);
        book.setProgrammingLanguage("Java");
        book.setFramework("Spring");
        book.setDifficulty("Intermediate");

        // 添加图书并获取ID
        Book savedBook = bookService.addBook(book);
        assertNotNull(savedBook, "保存的图书不能为null");
        assertNotNull(savedBook.getId(), "保存的图书ID不能为null");
        Long bookId = savedBook.getId();
        
        // 验证初始库存
        Optional<Book> initialBook = bookService.findById(bookId);
        assertTrue(initialBook.isPresent(), "应该能找到保存的图书");
        assertEquals(10, initialBook.get().getStock(), "初始库存应该是10");

        // 更新库存
        int newStock = 5;
        bookService.updateStock(bookId, newStock);

        // 直接从数据库查询验证
        try (Connection conn = DBHandler.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT stock FROM books WHERE id = ?")) {
            stmt.setLong(1, bookId);
            ResultSet rs = stmt.executeQuery();
            assertTrue(rs.next(), "应该能找到图书记录");
            assertEquals(newStock, rs.getInt("stock"), "库存应该更新为" + newStock);
            conn.commit();  // 确保查询操作被提交
        } catch (SQLException e) {
            fail("Failed to verify stock update: " + e.getMessage());
        }
    }

    @Test
    void testFindByIsbn() {
        // 添加测试图书
        String testIsbn = "TEST-" + System.currentTimeMillis();
        ComputerBook book = new ComputerBook();
        book.setIsbn(testIsbn);
        book.setTitle("Java编程思想");
        book.setAuthor("Bruce Eckel");
        book.setType("Computer");
        book.setStock(10);
        // 添加必要的计算机类图书属性
        book.setProgrammingLanguage("Java");
        book.setFramework("Spring");
        book.setDifficulty("Intermediate");

        Book savedBook = bookService.addBook(book);
        assertNotNull(savedBook, "图书添加应该成功");

        // 测试查找
        Optional<Book> found = bookService.findByIsbn(testIsbn);
        assertTrue(found.isPresent(), "应该能通过ISBN找到图书");
        assertEquals(testIsbn, found.get().getIsbn(), "ISBN应该匹配");
    }

    @Test
    void testDeleteBook() {
        // 添加测试图书
        ComputerBook book = new ComputerBook();
        book.setIsbn("TEST-" + System.currentTimeMillis());
        book.setTitle("Java编程思想");
        book.setAuthor("Bruce Eckel");
        book.setType("Computer");
        book.setStock(10);
        // 添加必要的计算机类图书属性
        book.setProgrammingLanguage("Java");
        book.setFramework("Spring");
        book.setDifficulty("Intermediate");

        Book savedBook = bookService.addBook(book);
        assertNotNull(savedBook, "图书添加应该成功");
        Long bookId = savedBook.getId();

        // 删除图书
        bookService.deleteBook(bookId);

        // 验证删除
        Optional<Book> found = bookService.findById(bookId);
        assertFalse(found.isPresent(), "图书应该已被删除");
    }
} 