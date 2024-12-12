package com.library.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.Properties;

public class DBHandler {
    private static final Logger logger = Logger.getLogger(DBHandler.class);
    private static HikariDataSource dataSource;
    
    static {
        initializeDataSource();
    }
    
    private static void initializeDataSource() {
        try {
            // 加载配置
            Properties props = loadProperties();
            logger.info("Successfully loaded properties");
            
            // 先尝试创建数据库
            createDatabaseIfNotExists(props);
            logger.info("Database check completed");
            
            // 配置连接池
            HikariConfig config = new HikariConfig();
            config.setDriverClassName(props.getProperty("db.driver"));
            config.setJdbcUrl(props.getProperty("db.url"));
            config.setUsername(props.getProperty("db.username"));
            config.setPassword(props.getProperty("db.password"));
            
            // 连接池设置
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(5);
            config.setIdleTimeout(300000);
            config.setConnectionTimeout(20000);
            config.setAutoCommit(false);
            
            // 创建数据源
            dataSource = new HikariDataSource(config);
            logger.info("Database connection pool initialized successfully");
            
        } catch (Exception e) {
            String errorMsg = "Failed to initialize database connection pool: " + e.getMessage();
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }
    
    private static Properties loadProperties() throws IOException {
        Properties props = new Properties();
        try (InputStream is = DBHandler.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (is == null) {
                throw new IOException("Cannot find application.properties");
            }
            props.load(is);
            return props;
        }
    }

    private static void createDatabaseIfNotExists(Properties props) {
        String url = props.getProperty("db.url");
        String baseUrl = url.substring(0, url.indexOf("?"));
        String dbName = baseUrl.substring(baseUrl.lastIndexOf("/") + 1);
        String baseDbUrl = baseUrl.substring(0, baseUrl.lastIndexOf("/"));
        
        try (Connection conn = DriverManager.getConnection(
                baseDbUrl, 
                props.getProperty("db.username"), 
                props.getProperty("db.password"))) {
            
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);
            logger.info("Database creation check completed");
            
        } catch (SQLException e) {
            String errorMsg = "Failed to create database: " + e.getMessage();
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            initializeDataSource();
        }
        return dataSource.getConnection();
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.error("Error closing connection", e);
            }
        }
    }

    public static void rollback(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                logger.error("Error rolling back transaction", e);
            }
        }
    }

    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
} 