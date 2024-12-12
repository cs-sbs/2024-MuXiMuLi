package com.library.repository.impl;

import com.library.domain.user.User;
import com.library.repository.UserRepository;
import com.library.util.DBHandler;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.Optional;

/**
 * 用户仓库实现类
 * 作用：用户数据访问实现
 * 功能：
 * - 实现用户的数据库操作
 * - 处理用户认证逻辑
 */
public class UserRepositoryImpl implements UserRepository {
    private static final Logger logger = Logger.getLogger(UserRepositoryImpl.class);

    @Override
    public User save(User user) {
        String sql = "INSERT INTO users (username, password, is_admin) VALUES (?, ?, ?)";
        
        try (Connection conn = DBHandler.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setBoolean(3, user.isAdmin());
            
            int affected = stmt.executeUpdate();
            if (affected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    user.setId(rs.getLong(1));
                    return user;
                }
            }
            logger.debug("User saved successfully: " + user.getUsername());
        } catch (SQLException e) {
            logger.error("Error saving user: " + user.getUsername(), e);
            throw new RuntimeException("Error saving user", e);
        }
        return null;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        
        try (Connection conn = DBHandler.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setAdmin(rs.getBoolean("is_admin"));
                logger.debug("Found user: " + username);
                return Optional.of(user);
            }
        } catch (SQLException e) {
            logger.error("Error finding user by username: " + username, e);
            throw new RuntimeException("Error finding user by username", e);
        }
        logger.debug("User not found: " + username);
        return Optional.empty();
    }

    @Override
    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        
        try (Connection conn = DBHandler.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            logger.error("Error checking user existence: " + username, e);
            throw new RuntimeException("Error checking user existence", e);
        }
        return false;
    }
} 