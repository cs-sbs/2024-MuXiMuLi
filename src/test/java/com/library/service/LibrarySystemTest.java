package com.library.service;

import com.library.domain.book.*;
import com.library.domain.user.User;
import com.library.repository.BookRepository;
import com.library.repository.UserRepository;
import com.library.repository.impl.BookRepositoryImpl;
import com.library.repository.impl.UserRepositoryImpl;
import com.library.service.impl.BookServiceImpl;
import com.library.service.impl.UserServiceImpl;
import com.library.util.DBHandler;
import org.junit.jupiter.api.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LibrarySystemTest {
    private static final Logger logger = LogManager.getLogger(LibrarySystemTest.class);
    private static BookService bookService;
    private static UserService userService;
    private static BackupService backupService;
    private static FileStorageService fileStorageService;
    private static String testBackupDir = "test_backup";

    @BeforeAll
    static void setUp() {
        // 初始化服务
        BookRepository bookRepository = new BookRepositoryImpl();
        UserRepository userRepository = new UserRepositoryImpl();
        bookService = new BookServiceImpl(bookRepository);
        userService = new UserServiceImpl(userRepository);
        fileStorageService = new FileStorageService(testBackupDir);
        backupService = new BackupService(bookService, fileStorageService, testBackupDir);
        
        // 创建测试目录
        new File(testBackupDir).mkdirs();
        
        // 清理数据库
        cleanDatabase();
    }

    private static void cleanDatabase() {
        try (Connection conn = DBHandler.getConnection();
             Statement stmt = conn.createStatement()) {
            // 禁用外键检查
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
            
            // 清空表
            stmt.execute("TRUNCATE TABLE books");
            stmt.execute("TRUNCATE TABLE users");
            stmt.execute("TRUNCATE TABLE backup_records");
            
            // 启用外键检查
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
            
            logger.info("Database cleaned successfully");
        } catch (Exception e) {
            logger.error("Error cleaning database", e);
            throw new RuntimeException("Failed to clean database", e);
        }
    }

    @BeforeEach
    void cleanBeforeTest() {
        cleanDatabase();
    }

    private String generateUniqueIsbn(String prefix) {
        return prefix + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMddHHmmss"));
    }

    private String generateTestUsername() {
        return "test" + (System.currentTimeMillis() % 10000);
    }

    @Test
    @Order(1)
    void testUserRegistrationAndLogin() {
        String testUsername = generateTestUsername();
        logger.info("Testing with username: {}", testUsername);
        
        // 测试用户注册
        User newUser = userService.register(testUsername, "password123");
        assertNotNull(newUser, "用户注册应该成功");
        assertFalse(newUser.isAdmin(), "新注册用户不应该是管理员");

        // 测试重复注册
        assertThrows(IllegalArgumentException.class, () -> 
            userService.register(testUsername, "password123"),
            "重复注册应该失败"
        );

        // 测试登录
        User loggedInUser = userService.login(testUsername, "password123");
        assertNotNull(loggedInUser, "用户登录���该成功");
        assertEquals(testUsername, loggedInUser.getUsername(), "登录用户名应该匹配");
    }

    @Test
    @Order(2)
    void testBookManagement() {
        String computerIsbn = generateUniqueIsbn("C");
        String literatureIsbn = generateUniqueIsbn("L");

        // 测试添加计算机类图书
        ComputerBook computerBook = new ComputerBook();
        computerBook.setIsbn(computerIsbn);
        computerBook.setTitle("Java编程思想");
        computerBook.setAuthor("Bruce Eckel");
        computerBook.setType("Computer");
        computerBook.setProgrammingLanguage("Java");
        computerBook.setFramework("None");
        computerBook.setDifficulty("Intermediate");
        computerBook.setStock(10);

        Book savedComputerBook = bookService.addBook(computerBook);
        assertNotNull(savedComputerBook, "图书添加应该成功");
        assertNotNull(savedComputerBook.getId(), "图书ID应该被分配");

        // 测试添加文学类图书
        LiteratureBook literatureBook = new LiteratureBook();
        literatureBook.setIsbn(literatureIsbn);
        literatureBook.setTitle("红楼梦");
        literatureBook.setAuthor("曹雪芹");
        literatureBook.setType("Literature");
        literatureBook.setGenre("Classical");
        literatureBook.setEra("Qing Dynasty");
        literatureBook.setLanguage("Chinese");
        literatureBook.setStock(5);

        Book savedLiteratureBook = bookService.addBook(literatureBook);
        assertNotNull(savedLiteratureBook, "图书添加应该成功");

        // 测试查找图书
        Optional<Book> foundBook = bookService.findByIsbn(computerIsbn);
        assertTrue(foundBook.isPresent(), "应该能找到已添加的图书");
        assertEquals("Java编程思想", foundBook.get().getTitle(), "图书标题应该匹配");

        // 测试更新库存
        bookService.updateStock(savedComputerBook.getId(), 5);
        foundBook = bookService.findById(savedComputerBook.getId());
        assertEquals(15, foundBook.get().getStock(), "库存应该正确更新");

        // 测试按类型查找
        List<Book> computerBooks = bookService.findByType("Computer");
        assertFalse(computerBooks.isEmpty(), "应该能找到计算机类图书");
        assertEquals(1, computerBooks.size(), "计算机类图书数量应该正确");

        // 测试删除图书
        bookService.deleteBook(savedComputerBook.getId());
        Optional<Book> deletedBook = bookService.findById(savedComputerBook.getId());
        assertFalse(deletedBook.isPresent(), "图书应该已被删除");
    }

    @Test
    @Order(3)
    void testBackupAndRestore() {
        // 添加测试数据
        String testIsbn = generateUniqueIsbn("TEST");
        ComputerBook testBook = new ComputerBook();
        testBook.setIsbn(testIsbn);
        testBook.setTitle("测试图书");
        testBook.setAuthor("测试作者");
        testBook.setType("Computer");
        testBook.setProgrammingLanguage("Java");
        testBook.setFramework("Spring");
        testBook.setDifficulty("Intermediate");
        testBook.setStock(1);
        
        Book savedBook = bookService.addBook(testBook);
        assertNotNull(savedBook, "图书应该成功保存");

        try {
            // 执行备份
            String backupFile = backupService.performBackup().get();
            assertNotNull(backupFile, "备份文件名不应为空");
            File backupFilePath = new File(testBackupDir, backupFile);
            assertTrue(backupFilePath.exists(), "备份文件应该存在");
            assertTrue(backupFilePath.length() > 0, "备份文件不应为空");

            // 清空数据库
            cleanDatabase();

            // 恢复备份
            int restoredCount = backupService.restoreFromBackup(backupFile).get();
            assertTrue(restoredCount > 0, "应该有图书被恢复");

            // 验证���复的数据
            Optional<Book> restoredBook = bookService.findByIsbn(testIsbn);
            assertTrue(restoredBook.isPresent(), "应该能找到恢复的图书");
            assertEquals("测试图书", restoredBook.get().getTitle(), "恢复的图书标题应该正确");

        } catch (Exception e) {
            logger.error("Backup test failed", e);
            fail("备份或恢复过程不应抛出异常: " + e.getMessage());
        }
    }

    @Test
    @Order(4)
    void testSearchFunctionality() {
        // 先添加一本测试图书
        String testIsbn = generateUniqueIsbn("TEST");
        ComputerBook testBook = new ComputerBook();
        testBook.setIsbn(testIsbn);
        testBook.setTitle("测试图书");
        testBook.setAuthor("测试作者");
        testBook.setType("Computer");
        testBook.setStock(1);
        bookService.addBook(testBook);

        // 测试按标题搜索
        List<Book> books = bookService.findByTitle("测试");
        assertTrue(!books.isEmpty(), "应该能找到包含关键词的图书");
        assertEquals(1, books.size(), "应该只找到一本图书");

        // 测试按ISBN搜索
        Optional<Book> book = bookService.findByIsbn(testIsbn);
        assertTrue(book.isPresent(), "应该能通过ISBN找到图书");

        // 测试按类型搜索
        List<Book> typeBooks = bookService.findByType("Computer");
        assertFalse(typeBooks.isEmpty(), "应该能找到指定类型的图书");
    }

    @Test
    @Order(5)
    void testErrorHandling() {
        try {
            // 先添加一本图书
            String testIsbn = generateUniqueIsbn("TEST");
            ComputerBook book = new ComputerBook();
            book.setIsbn(testIsbn);
            book.setTitle("测试图书");
            book.setAuthor("测试作者");
            book.setType("Computer");
            book.setStock(1);
            Book savedBook = bookService.addBook(book);
            assertNotNull(savedBook, "图书应该成功保存");

            // 测试添加重复ISBN的图书
            ComputerBook duplicateBook = new ComputerBook();
            duplicateBook.setIsbn(testIsbn); // 使用相同的ISBN
            duplicateBook.setTitle("重复图书");
            duplicateBook.setAuthor("测试作者");
            duplicateBook.setType("Computer");
            duplicateBook.setStock(1);

            // 捕获并验证异常
            Exception exception = assertThrows(RuntimeException.class, () -> 
                bookService.addBook(duplicateBook),
                "重复的ISBN应该抛出异常"
            );

            // 获取完整的异常链中的所有消息
            String fullErrorMessage = getFullErrorMessage(exception);
            
            assertTrue(
                fullErrorMessage.contains("Duplicate") || 
                fullErrorMessage.contains("重复") || 
                fullErrorMessage.contains("duplicate") || 
                fullErrorMessage.contains("已存在") ||
                fullErrorMessage.contains("SQLIntegrityConstraintViolationException"),
                "异常信息应该包含重复项提示: " + fullErrorMessage
            );

            // 测试空ISBN
            assertThrows(IllegalArgumentException.class, () -> 
                bookService.findByIsbn(null),
                "空ISBN应该抛出异常"
            );

            // 测试空标题
            assertThrows(IllegalArgumentException.class, () -> 
                bookService.findByTitle(null),
                "空标题应该抛出异常"
            );

            // 测试无效的图书ID
            assertThrows(IllegalArgumentException.class, () -> 
                bookService.updateStock(null, 1),
                "无效的图书ID应该抛出异常"
            );

        } catch (Exception e) {
            logger.error("Error handling test failed", e);
            fail("错误处理测试不应抛出异常: " + e.getMessage());
        }
    }

    // 添加辅助方法来获取完整的异常消息
    private String getFullErrorMessage(Throwable throwable) {
        StringBuilder message = new StringBuilder();
        while (throwable != null) {
            if (throwable.getMessage() != null) {
                message.append(throwable.getMessage()).append("; ");
            }
            if (throwable.getClass().getSimpleName() != null) {
                message.append(throwable.getClass().getSimpleName()).append("; ");
            }
            throwable = throwable.getCause();
        }
        return message.toString();
    }

    @Test
    @Order(6)
    void testSystemStatus() {
        // 测试系统状态
        List<Book> allBooks = bookService.findAll();
        assertNotNull(allBooks, "应该能获取所有图书列表");

        List<String> backups = backupService.listBackups();
        assertNotNull(backups, "应该能获取备份列表");
        assertFalse(backups.isEmpty(), "应该有备份文件");
    }

    @AfterAll
    static void tearDown() {
        // 清理测试数据
        cleanDatabase();
        
        // 清理测试目录
        deleteDirectory(new File(testBackupDir));
    }

    private static void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }
} 