package com.library.domain.user;

import lombok.Data;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private boolean admin;
} 