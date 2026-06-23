package com.example.auth_starter.infrastructure.adapter.out.persistence;

import java.util.Set;
import java.util.UUID;

import com.example.auth_starter.domain.model.Role;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserJpaEntity {
    @Id
    private UUID id;

    @Column(unique = true)
    private String email;

    @Column
    private String password;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;
}
