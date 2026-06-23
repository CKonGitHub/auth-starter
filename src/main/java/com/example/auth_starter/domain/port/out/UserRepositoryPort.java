package com.example.auth_starter.domain.port.out;

import java.util.Optional;

import com.example.auth_starter.domain.model.User;

public interface UserRepositoryPort {
    User save(User user);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
