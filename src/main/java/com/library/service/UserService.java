package com.library.service;

import com.library.domain.user.User;

public interface UserService {
    User register(String username, String password);
    User login(String username, String password);
} 