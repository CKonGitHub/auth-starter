package com.example.auth_starter.infrastructure.adapter.out.persistence;

import org.springframework.stereotype.Component;

import com.example.auth_starter.domain.model.User;

@Component
public class UserPersistenceMapper {
    public UserJpaEntity toJpaEntity(User user) {
        return new UserJpaEntity(user.getId(), user.getEmail(), user.getPassword(), user.getRoles());
    }

    public User toUser(UserJpaEntity userJpaEntity) {
        return new User(userJpaEntity.getId(), userJpaEntity.getEmail(), userJpaEntity.getPassword(),
                userJpaEntity.getRoles());
    }
}
