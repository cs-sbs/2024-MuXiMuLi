package com.library.service.impl;

import com.library.domain.user.User;
import com.library.repository.UserRepository;
import com.library.service.UserService;
import org.apache.log4j.Logger;

public class UserServiceImpl implements UserService {
    private static final Logger logger = Logger.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User login(String username, String password) {
        return userRepository.findByUsername(username)
            .filter(user -> user.getPassword().equals(password))
            .orElseThrow(() -> new IllegalArgumentException("用户名或密码错误"));
    }

    @Override
    public User register(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }
        
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("用户名已存在");
        }
        
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setAdmin(false);
        
        return userRepository.save(user);
    }
} 