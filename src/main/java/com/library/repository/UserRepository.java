package com.library.repository;

import com.library.domain.user.User;
import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
} 