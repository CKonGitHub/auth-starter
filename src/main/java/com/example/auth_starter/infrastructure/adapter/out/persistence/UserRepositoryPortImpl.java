package com.example.auth_starter.infrastructure.adapter.out.persistence;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.auth_starter.domain.model.User;
import com.example.auth_starter.domain.port.out.UserRepositoryPort;

@Repository
public class UserRepositoryPortImpl implements UserRepositoryPort {
    private final UserJpaRepository userJpaRepository;
    private final UserPersistenceMapper mapper;

    public UserRepositoryPortImpl(UserJpaRepository userJpaRepository, UserPersistenceMapper userPersistenceMapper) {
        this.mapper = userPersistenceMapper;
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public User save(User user) {
        return mapper.toUser(userJpaRepository.save(mapper.toJpaEntity(user)));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email)
                .map(mapper::toUser);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

}
