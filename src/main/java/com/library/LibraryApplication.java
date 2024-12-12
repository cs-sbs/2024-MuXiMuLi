package com.library;

import com.library.controller.LibraryController;
import com.library.repository.BookRepository;
import com.library.repository.UserRepository;
import com.library.repository.impl.BookRepositoryImpl;
import com.library.repository.impl.UserRepositoryImpl;
import com.library.service.BackupService;
import com.library.service.BookService;
import com.library.service.FileStorageService;
import com.library.service.UserService;
import com.library.service.impl.BookServiceImpl;
import com.library.service.impl.UserServiceImpl;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class LibraryApplication {
    private static final Logger logger = Logger.getLogger(LibraryApplication.class);
    private static volatile boolean running = true;

    public static void main(String[] args) {
        try {
            // 清理控制台
            clearConsole();
            

            registerShutdownHook();
            
            // 注册Ctrl+C处理器
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                running = false;
                logger.info("Shutting down application...");
            }));
            
            // 加载配置
            Properties props = loadProperties("application.properties");
            
            // 初始化组件
            initializeComponents(props);
            
            // 等待2秒，确保初始化日志显示完成
            Thread.sleep(2000);
            
            // 清理控制台，准备显示菜单
            clearConsole();
            
            // 启动控制器
            startController(props);
            
        } catch (Exception e) {
            logger.error("Failed to start library application", e);
            System.err.println("程序启动失败：" + e.getMessage());
            System.exit(1);
        }
    }
    
    private static void registerShutdownHook() {
        // 注册信号处理器
        sun.misc.Signal.handle(new sun.misc.Signal("INT"), signal -> {
            System.out.println("\n正在安全退出程序...");
            setRunning(false);
            // 给一些时间让程序完成清理工作
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.exit(0);
        });
    }
    
    private static void initializeComponents(Properties props) throws Exception {
        // 初始化存储库
        BookRepository bookRepository = new BookRepositoryImpl();
        UserRepository userRepository = new UserRepositoryImpl();
        
        // 初始化服务
        BookService bookService = new BookServiceImpl(bookRepository);
        UserService userService = new UserServiceImpl(userRepository);
        
        // 初始化存储服务
        String storageDir = initializeStorageDir(props);
        FileStorageService fileStorageService = new FileStorageService(storageDir);
        
        // 初始化并启动备份服务
        BackupService backupService = new BackupService(
            bookService, 
            fileStorageService,
            storageDir
        );
        
        // 启动定期备份
        int interval = Integer.parseInt(props.getProperty("backup.interval", "24"));
        TimeUnit timeUnit = TimeUnit.valueOf(props.getProperty("backup.timeunit", "HOURS"));
        backupService.startScheduledBackup(interval, timeUnit);
        
        // 存储服务实例供后续使用
        ApplicationContext.init(bookService, userService, backupService);
    }
    
    private static String initializeStorageDir(Properties props) {
        String storageDir = props.getProperty("storage.dir", "backup")
            .trim()
            .replace("/", File.separator)
            .replace("\\", File.separator);
        
        if (!Paths.get(storageDir).isAbsolute()) {
            storageDir = Paths.get(System.getProperty("user.dir"), storageDir)
                .normalize()
                .toString();
        }
        
        return storageDir;
    }
    
    private static void startController(Properties props) {
        LibraryController controller = new LibraryController(
            ApplicationContext.getBookService(),
            ApplicationContext.getUserService(),
            ApplicationContext.getBackupService()
        );
        
        // 在新线程中运行控制器
        Thread controllerThread = new Thread(() -> {
            try {
                controller.start();
            } catch (Exception e) {
                logger.error("Controller error", e);
            }
        });
        controllerThread.start();
        
        // 等待程序退出信号
        while (running) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        // 清理资源
        try {
            ApplicationContext.getBackupService().shutdown();
            // 等待控制器线程结束
            controllerThread.join(5000);
        } catch (Exception e) {
            logger.error("Error during shutdown", e);
        }
        
        logger.info("Application shutdown complete");
    }
    
    private static void clearConsole() {
        try {
            final String os = System.getProperty("os.name");
            if (os.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            // 如果清屏失败，打印多个换行符
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }
    
    private static Properties loadProperties(String filename) throws IOException {
        Properties props = new Properties();
        try (InputStream input = LibraryApplication.class.getClassLoader()
                .getResourceAsStream(filename)) {
            if (input == null) {
                throw new IOException("Unable to find " + filename);
            }
            props.load(input);
        }
        return props;
    }

    // 添加静态方法来检查运行状态
    public static boolean isRunning() {
        return running;
    }

    // 添加静态方法来设置运行状态
    public static void setRunning(boolean status) {
        running = status;
    }

} 