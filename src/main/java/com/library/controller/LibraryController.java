package com.library.controller;

import com.library.domain.book.*;
import com.library.domain.user.User;
import com.library.service.BackupService;
import com.library.service.BookService;
import com.library.service.UserService;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class LibraryController {
    private static final Logger logger = Logger.getLogger(LibraryController.class);
    private final BookService bookService;
    private final UserService userService;
    private final BackupService backupService;
    private final Scanner scanner;
    private User currentUser;
    private volatile boolean running = true;

    public LibraryController(BookService bookService, UserService userService, BackupService backupService) {
        this.bookService = bookService;
        this.userService = userService;
        this.backupService = backupService;
        this.scanner = new Scanner(System.in);
        this.currentUser = null;
    }

    public void start() {
        while (!shouldExit()) {
            try {
                if (currentUser == null) {
                    showLoginMenu();
                } else {
                    showMainMenu();
                }
                
                if (shouldExit()) {
                    break;
                }
                
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    continue;
                }
                
                try {
                    int choice = Integer.parseInt(input);
                    if (currentUser == null) {
                        processLoginChoice(choice);
                    } else {
                        processMainChoice(choice);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("请输入有效的数字选项！");
                }
                
            } catch (Exception e) {
                if (shouldExit()) {
                    break;
                }
                logger.error("Error in controller", e);
                System.out.println("发生错误，请重试！");
                // 清除输入缓冲区
                scanner.nextLine();
            }
        }
        
        System.out.println("\n感谢使用！再见！");
    }

    private boolean shouldExit() {
        return !running || Thread.currentThread().isInterrupted();
    }

    private void showMainMenu() {
        if (currentUser.isAdmin()) {
            showSystemStatus();
        }
        
        System.out.println("\n=== 图书管理系统 ===");
        System.out.println("当前用户: " + currentUser.getUsername() + 
                          (currentUser.isAdmin() ? " (管理员)" : " (普通用户)"));
        System.out.println("------------------------");
        System.out.println("1. 浏览图书");
        System.out.println("2. 搜索图书");
        if (currentUser.isAdmin()) {
            System.out.println("3. 添加图书");
            System.out.println("4. 删除图书");
            System.out.println("5. 更新库存");
            System.out.println("6. 备份管理");
        }
        System.out.println("7. 个人信息");
        System.out.println("8. 注销");
        System.out.println("0. 退出");
        System.out.print("请选择: ");
    }

    // 其他方法中的输入处理也要类似修改
    private int getIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("请输入有效的数字！");
            }
        }
    }

    private String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private void showLoginMenu() {
        System.out.println("\n=== 图书管理系统 ===");
        System.out.println("1. 注册");
        System.out.println("2. 登录");
        System.out.println("3. 浏览图书");
        System.out.println("4. 搜索图书");
        System.out.println("0. 退出");
        System.out.print("请选择: ");
    }

    private void processLoginChoice(int choice) {
        switch (choice) {
            case 1 -> register();
            case 2 -> login();
            case 3 -> listBooks();
            case 4 -> searchBooks();
            case 0 -> {
                System.out.println("感谢使用！");
                running = false;
            }
            default -> System.out.println("无效选择，请重试");
        }
    }

    private void register() {
        System.out.println("\n=== 用户注册 ===");
        String username = getStringInput("用户名: ");
        String password = getStringInput("密码: ");
        
        try {
            User user = userService.register(username, password);
            System.out.println("注册成功！");
            currentUser = user;
            logger.info("User registered successfully: " + username);
        } catch (Exception e) {
            System.out.println("注册失败：" + e.getMessage());
            logger.error("Registration failed for user: " + username, e);
        }
    }

    private void login() {
        System.out.println("\n=== 用户登录 ===");
        String username = getStringInput("用户名: ");
        String password = getStringInput("密码: ");
        
        try {
            User user = userService.login(username, password);
            currentUser = user;
            System.out.println("登录成功！");
            if (user.isAdmin()) {
                System.out.println("您以管理员身份登录，可以使用所有功能");
            } else {
                System.out.println("您以普通用户身份登录，部分功能可能受限");
            }
            logger.debug("User logged in: " + username);
        } catch (IllegalArgumentException e) {
            System.out.println("登录失败：用户名或密码错误");
            logger.debug("Login failed for user: " + username);
        } catch (Exception e) {
            System.out.println("登录失败：系统错误，请稍后再试");
            logger.error("Unexpected error during login", e);
        }
    }

    private void processMainChoice(int choice) {
        switch (choice) {
            case 1 -> listBooks();
            case 2 -> searchBooks();
            case 3 -> {
                if (currentUser.isAdmin()) addBook();
                else System.out.println("权限不足！");
            }
            case 4 -> {
                if (currentUser.isAdmin()) deleteBook();
                else System.out.println("权限不足！");
            }
            case 5 -> {
                if (currentUser.isAdmin()) updateStock();
                else System.out.println("权限不足！");
            }
            case 6 -> {
                if (currentUser.isAdmin()) manageBackups();
                else System.out.println("权限不足！");
            }
            case 7 -> showUserInfo();
            case 8 -> logout();
            case 0 -> {
                System.out.println("感谢使用！");
                running = false;
            }
            default -> System.out.println("无效选择，请重试");
        }
    }

    public void logout() {
        currentUser = null;
        System.out.println("已注销");
        logger.info("User logged out");
    }

    public void showUserInfo() {
        System.out.println("\n=== 个人信息 ===");
        System.out.println("用户名: " + currentUser.getUsername());
        System.out.println("角色: " + (currentUser.isAdmin() ? "管理员" : "普通用户"));
    }

    private void showSystemStatus() {
        if (currentUser != null && currentUser.isAdmin()) {
            System.out.println("\n=== 系统状态 ===");
            try {
                List<Book> books = bookService.findAll();
                List<String> backups = backupService.listBackups();
                
                System.out.println("图书总数: " + books.size());
                System.out.println("备份文件数: " + backups.size());
                if (!backups.isEmpty()) {
                    System.out.println("最新备份: " + backups.get(backups.size() - 1));
                } else {
                    System.out.println("最新备份: 无");
                }
                
                // 检查数据库连接
                try {
                    bookService.findAll();
                    System.out.println("数据库连接状态: 正常");
                } catch (Exception e) {
                    System.out.println("数据库连接状态: 异常");
                    logger.error("Database connection check failed", e);
                }
                
                // 检查系统运行状态
                Runtime runtime = Runtime.getRuntime();
                long totalMemory = runtime.totalMemory() / (1024 * 1024);
                long freeMemory = runtime.freeMemory() / (1024 * 1024);
                long usedMemory = totalMemory - freeMemory;
                
                System.out.println("系统运行状态: 正常");
                System.out.println("系统内存使用: " + usedMemory + "MB/" + totalMemory + "MB");
                
                // 检查备份目录
                File backupDir = new File("backup");
                if (backupDir.exists() && backupDir.isDirectory()) {
                    long freeSpace = backupDir.getFreeSpace() / (1024 * 1024);
                    System.out.println("备份目录可用空间: " + freeSpace + "MB");
                }
                
            } catch (Exception e) {
                System.out.println("获取系统状态失败：" + e.getMessage());
                logger.error("Failed to get system status", e);
            }
        }
    }

    public void listBooks() {
        System.out.println("\n=== 图书列表 ===");
        System.out.println("1. 查看所有图书");
        System.out.println("2. 按类型浏览");
        System.out.println("0. 返回上级菜单");
        System.out.print("请选择: ");
        
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            switch (choice) {
                case 1 -> showAllBooks();
                case 2 -> showBooksByType();
                case 0 -> { return; }
                default -> System.out.println("无效选择，请重试");
            }
        } catch (NumberFormatException e) {
            System.out.println("请输入有效的数字！");
        }
    }

    private void showAllBooks() {
        try {
            List<Book> books = bookService.findAll();
            if (books.isEmpty()) {
                System.out.println("\n图书馆暂无图书！");
                return;
            }

            System.out.println("\n=== 所有图书 ===");
            System.out.println("共找到 " + books.size() + " 本图书：\n");
            
            // 按类型分组显示
            Map<String, List<Book>> booksByType = books.stream()
                .collect(Collectors.groupingBy(Book::getType));
            
            booksByType.forEach((type, typeBooks) -> {
                System.out.printf("\n=== %s类图书 (%d本) ===\n", 
                    getChineseTypeName(type), typeBooks.size());
                typeBooks.forEach(book -> {
                    System.out.println(book.displayInfo());
                    System.out.println("------------------------");
                });
            });
            
        } catch (Exception e) {
            System.out.println("获取图书列表失败：" + e.getMessage());
            logger.error("Error getting all books", e);
        }
    }

    private void showBooksByType() {
        System.out.println("\n=== 按类型浏览图书 ===");
        System.out.println("1. 计算机类 (Computer)");
        System.out.println("2. 文学类 (Literature)");
        System.out.println("3. 科学类 (Science)");
        System.out.println("4. 艺术类 (Art)");
        System.out.println("5. 历史类 (History)");
        System.out.println("6. 哲学类 (Philosophy)");
        System.out.println("7. 经济类 (Economics)");
        System.out.println("8. 医学类 (Medicine)");
        System.out.println("9. 教育类 (Education)");
        System.out.println("10. 法律类 (Law)");
        System.out.println("0. 返回上级菜单");
        
        try {
            int choice = getIntInput("请选择图书类型: ");
            String type = switch (choice) {
                case 1 -> "Computer";
                case 2 -> "Literature";
                case 3 -> "Science";
                case 4 -> "Art";
                case 5 -> "History";
                case 6 -> "Philosophy";
                case 7 -> "Economics";
                case 8 -> "Medicine";
                case 9 -> "Education";
                case 10 -> "Law";
                case 0 -> null;
                default -> throw new IllegalArgumentException("无效的选择");
            };
            
            if (type != null) {
                List<Book> books = bookService.findByType(type);
                if (books.isEmpty()) {
                    System.out.println("\n该类型暂无图书！");
                } else {
                    System.out.printf("\n=== %s类图书 ===\n", getChineseTypeName(type));
                    System.out.println("找到 " + books.size() + " 本图书：\n");
                    for (Book book : books) {
                        System.out.println(book.displayInfo());
                        System.out.println("------------------------");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("浏览图书失败：" + e.getMessage());
            logger.error("Error browsing books by type", e);
        }
    }

    private String getChineseTypeName(String type) {
        return switch (type) {
            case "Computer" -> "计算机";
            case "Literature" -> "文学";
            case "Science" -> "科学";
            case "Art" -> "艺术";
            case "History" -> "历史";
            case "Philosophy" -> "哲学";
            case "Economics" -> "经济";
            case "Medicine" -> "医学";
            case "Law" -> "法律";
            case "Education" -> "教育";
            default -> type;
        };
    }

    public void searchBooks() {
        System.out.println("\n=== 搜索图书 ===");
        System.out.println("1. 按ISBN搜索");
        System.out.println("2. 按关键字搜索");
        System.out.println("3. 按类型搜索");
        System.out.println("4. 组合搜索");
        System.out.println("0. 返回上级菜单");
        System.out.print("请选择: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            String keyword = null;
            String type = null;
            String isbn = null;

            switch (choice) {
                case 1 -> {
                    isbn = getStringInput("请输入ISBN: ");
                    bookService.findByIsbn(isbn).ifPresentOrElse(
                        book -> {
                            System.out.println("\n找到图书：");
                            System.out.println(book.displayInfo());
                        },
                        () -> System.out.println("未找到该图书！")
                    );
                    return;
                }
                case 2 -> {
                    keyword = getStringInput("请输入搜索关键字(标题或作者): ");
                }
                case 3 -> {
                    System.out.println("\n可选图书类型：");
                    for (String bookType : new String[]{"Computer", "Literature", "Science", 
                        "Art", "History", "Philosophy", "Economics", "Medicine", "Education"}) {
                        System.out.println(bookType);
                    }
                    type = getStringInput("请输入图书类型: ").toUpperCase();
                }
                case 4 -> {
                    keyword = getStringInput("请输入搜索关键字(标题或作者): ");
                    System.out.println("\n可选图书类型：");
                    for (String bookType : new String[]{"Computer", "Literature", "Science", 
                        "Art", "History", "Philosophy", "Economics", "Medicine", "Education"}) {
                        System.out.println(bookType);
                    }
                    type = getStringInput("请输入图书类型: ").toUpperCase();
                }
                case 0 -> { return; }
                default -> {
                    System.out.println("无效选择！");
                    return;
                }
            }

            // 只有在非ISBN搜索时才执行通用搜索
            if (choice != 1) {
                List<Book> results = bookService.searchBooks(keyword, type);
                
                if (results.isEmpty()) {
                    System.out.println("\n未找到匹配的图书！");
                    return;
                }

                System.out.printf("\n找到 %d 本匹配的图书：\n", results.size());
                for (Book book : results) {
                    System.out.println("\n" + book.displayInfo());
                }
            }

        } catch (NumberFormatException e) {
            System.out.println("请输入有效的数字！");
        } catch (Exception e) {
            System.out.println("搜索失败：" + e.getMessage());
            logger.error("Error during book search", e);
        }
    }

    public void addBook() {
        System.out.println("\n=== 添加图书 ===");
        System.out.println("请选择图书类型：");
        System.out.println("1. 计算机类 (Computer)");
        System.out.println("2. 文学类 (Literature)");
        System.out.println("3. 科学类 (Science)");
        System.out.println("4. 艺术类 (Art)");
        System.out.println("5. 历史类 (History)");
        System.out.println("6. 哲学类 (Philosophy)");
        System.out.println("7. 经济类 (Economics)");
        System.out.println("8. 医学类 (Medicine)");
        System.out.println("9. 教育类 (Education)");
        System.out.println("10. 法律类 (Law)");
        System.out.println("0. 返回上级菜单");
        
        try {
            int choice = getIntInput("请选择: ");
            Book book = switch (choice) {
                case 1 -> addComputerBook();
                case 2 -> addLiteratureBook();
                case 3 -> addScienceBook();
                case 4 -> addArtBook();
                case 5 -> addHistoryBook();
                case 6 -> addPhilosophyBook();
                case 7 -> addEconomicsBook();
                case 8 -> addMedicineBook();
                case 9 -> addEducationBook();
                case 10 -> addLawBook();
                case 0 -> null;
                default -> throw new IllegalArgumentException("无效的选择");
            };
            
            if (book != null) {
                Book savedBook = bookService.addBook(book);
                if (savedBook != null) {
                    System.out.println("图书添加成功！");
                    System.out.println("\n图书信息：");
                    System.out.println(savedBook.displayInfo());
                } else {
                    System.out.println("图书添加失败！");
                }
            }
        } catch (Exception e) {
            System.out.println("添加图书失败：" + e.getMessage());
            logger.error("Error adding book", e);
        }
    }

    private ComputerBook addComputerBook() {
        ComputerBook book = new ComputerBook();
        book.setIsbn(getStringInput("ISBN (C开头+3位数字): "));
        book.setTitle(getStringInput("书名: "));
        book.setAuthor(getStringInput("作者: "));
        book.setProgrammingLanguage(getStringInput("编程语言: "));
        book.setFramework(getStringInput("框架: "));
        book.setDifficulty(getStringInput("难度等级: "));
        book.setStock(getIntInput("库存数量: "));
        return book;
    }

    private LiteratureBook addLiteratureBook() {
        LiteratureBook book = new LiteratureBook();
        book.setIsbn(getStringInput("ISBN (L开头+3位数字): "));
        book.setTitle(getStringInput("书名: "));
        book.setAuthor(getStringInput("作者: "));
        book.setGenre(getStringInput("文学流派: "));
        book.setEra(getStringInput("年代: "));
        book.setLanguage(getStringInput("语言: "));
        book.setStock(getIntInput("库存数量: "));
        return book;
    }

    private ScienceBook addScienceBook() {
        ScienceBook book = new ScienceBook();
        book.setIsbn(getStringInput("ISBN (S开头+3位数字): "));
        book.setTitle(getStringInput("书名: "));
        book.setAuthor(getStringInput("作者: "));
        book.setSubjectArea(getStringInput("学科领域: "));
        book.setResearchField(getStringInput("研究方向: "));
        book.setAcademicLevel(getStringInput("学术水平: "));
        book.setStock(getIntInput("库存数量: "));
        return book;
    }

    private ArtBook addArtBook() {
        ArtBook book = new ArtBook();
        book.setIsbn(getStringInput("ISBN (A开头+3位数字): "));
        book.setTitle(getStringInput("书名: "));
        book.setAuthor(getStringInput("作者: "));
        book.setArtForm(getStringInput("艺术形式: "));
        book.setMedium(getStringInput("创作媒介: "));
        book.setStyle(getStringInput("艺术风格: "));
        book.setStock(getIntInput("库存数量: "));
        return book;
    }

    private HistoryBook addHistoryBook() {
        HistoryBook book = new HistoryBook();
        book.setIsbn(getStringInput("ISBN (H开头+3位数字): "));
        book.setTitle(getStringInput("书名: "));
        book.setAuthor(getStringInput("作者: "));
        book.setTimePeriod(getStringInput("时代: "));
        book.setRegion(getStringInput("地区: "));
        book.setHistoricalFigures(getStringInput("历史人物: "));
        book.setStock(getIntInput("库存数量: "));
        return book;
    }

    private PhilosophyBook addPhilosophyBook() {
        PhilosophyBook book = new PhilosophyBook();
        book.setIsbn(getStringInput("ISBN (P开头+3位数字): "));
        book.setTitle(getStringInput("书名: "));
        book.setAuthor(getStringInput("作者: "));
        book.setPhilosophicalSchool(getStringInput("哲学流派: "));
        book.setKeyConcepts(getStringInput("核心概念: "));
        book.setThinkers(getStringInput("主要思想家: "));
        book.setStock(getIntInput("库存数量: "));
        return book;
    }

    private EconomicsBook addEconomicsBook() {
        EconomicsBook book = new EconomicsBook();
        book.setIsbn(getStringInput("ISBN (E开头+3位数字): "));
        book.setTitle(getStringInput("书名: "));
        book.setAuthor(getStringInput("作者: "));
        book.setEconomicSchool(getStringInput("经济学派: "));
        book.setMarketType(getStringInput("市场类型: "));
        book.setApplicationField(getStringInput("应用领域: "));
        book.setStock(getIntInput("库存数量: "));
        return book;
    }

    private MedicineBook addMedicineBook() {
        MedicineBook book = new MedicineBook();
        book.setIsbn(getStringInput("ISBN (M开头+3位数字): "));
        book.setTitle(getStringInput("书名: "));
        book.setAuthor(getStringInput("作者: "));
        book.setMedicalSpecialty(getStringInput("医学专业: "));
        book.setClinicalFocus(getStringInput("临床方向: "));
        book.setPracticeArea(getStringInput("实践领域: "));
        book.setStock(getIntInput("库存数量: "));
        return book;
    }

    private EducationBook addEducationBook() {
        EducationBook book = new EducationBook();
        book.setIsbn(getStringInput("ISBN (D开头+3位数字): "));
        book.setTitle(getStringInput("书名: "));
        book.setAuthor(getStringInput("作者: "));
        book.setEducationLevel(getStringInput("教育层次: "));
        book.setSubject(getStringInput("学科: "));
        book.setTeachingMethod(getStringInput("教学方法: "));
        book.setStock(getIntInput("库存数量: "));
        return book;
    }

    private LawBook addLawBook() {
        LawBook book = new LawBook();
        book.setIsbn(getStringInput("ISBN (W开头+3位数字): "));
        book.setTitle(getStringInput("书名: "));
        book.setAuthor(getStringInput("作者: "));
        book.setLegalSystem(getStringInput("法律体系: "));
        book.setJurisdiction(getStringInput("司法管辖: "));
        book.setLegalField(getStringInput("法律领域: "));
        book.setStock(getIntInput("库存数量: "));
        return book;
    }

    public void deleteBook() {
        String isbn = getStringInput("\n请输入要删除的图书ISBN: ");
        try {
            bookService.findByIsbn(isbn).ifPresentOrElse(
                book -> {
                    System.out.println("\n找到图书：");
                    System.out.println(book.displayInfo());
                    System.out.print("确认删除？(y/n): ");
                    String confirm = scanner.nextLine().trim().toLowerCase();
                    if (confirm.equals("y")) {
                        bookService.deleteBook(book.getId());
                        System.out.println("图书删除成功！");
                    } else {
                        System.out.println("已取消删除操作。");
                    }
                },
                () -> System.out.println("未找到该图书！")
            );
        } catch (Exception e) {
            System.out.println("删除图书失败：" + e.getMessage());
            logger.error("Error deleting book: " + isbn, e);
        }
    }

    public void updateStock() {
        String isbn = getStringInput("\n请输入要更新库存的图书ISBN: ");
        try {
            bookService.findByIsbn(isbn).ifPresentOrElse(
                book -> {
                    System.out.println("\n当前图书息：");
                    System.out.println(book.displayInfo());
                    System.out.print("输入库存变化量（正数增加，负数减少）: ");
                    try {
                        int change = Integer.parseInt(scanner.nextLine().trim());
                        bookService.updateStock(book.getId(), change);
                        System.out.println("库存更新成功！");
                    } catch (NumberFormatException e) {
                        System.out.println("请输入有效的数字！");
                    }
                },
                () -> System.out.println("未找到该书！")
            );
        } catch (Exception e) {
            System.out.println("更新库存失败：" + e.getMessage());
            logger.error("Error updating stock for book: " + isbn, e);
        }
    }

    public void manageBackups() {
        while (true) {
            System.out.println("\n=== 备份管理 ===");
            System.out.println("1. 查看备份列表");
            System.out.println("2. 执行自动备份");
            System.out.println("3. 恢复备份");
            System.out.println("0. 返回上级菜单");
            System.out.print("请选择: ");
            
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                switch (choice) {
                    case 1 -> listBackups();
                    case 2 -> performManualBackup();
                    case 3 -> restoreFromBackup();
                    case 0 -> { return; }
                    default -> System.out.println("无效选择，请重试");
                }
            } catch (NumberFormatException e) {
                System.out.println("请输入有效的数字！");
            }
        }
    }

    private void listBackups() {
        List<String> backups = backupService.listBackups();
        if (backups.isEmpty()) {
            System.out.println("暂无备份文件");
            return;
        }
        
        System.out.println("\n=== 备份文件列表 ===");
        for (int i = 0; i < backups.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, backups.get(i));
        }
    }

    private void performManualBackup() {
        System.out.println("\n执行手动备份...");
        try {
            String filename = backupService.performBackup().get();
            System.out.println("备份完成：" + filename);
        } catch (Exception e) {
            System.out.println("备份失败：" + e.getMessage());
            logger.error("Manual backup failed", e);
        }
    }

    private void restoreFromBackup() {
        List<String> backups = backupService.listBackups();
        if (backups.isEmpty()) {
            System.out.println("暂无可用的备份文件");
            return;
        }
        
        System.out.println("\n=== 选择要恢复的备份文件 ===");
        for (int i = 0; i < backups.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, backups.get(i));
        }
        
        System.out.print("请选择备份文件编号: ");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice < 1 || choice > backups.size()) {
                System.out.println("无效的选择");
                return;
            }
            
            String filename = backups.get(choice - 1);
            System.out.println("正在恢复备份：" + filename);
            
            int restoredCount = backupService.restoreFromBackup(filename).get();
            System.out.println("恢复完成，共恢复 " + restoredCount + " 本图书");
        } catch (Exception e) {
            System.out.println("恢复失败：" + e.getMessage());
            logger.error("Backup restoration failed", e);
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
} 