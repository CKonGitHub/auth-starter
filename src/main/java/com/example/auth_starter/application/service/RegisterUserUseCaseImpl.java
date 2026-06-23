package com.example.auth_starter.application.service;

import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.auth_starter.domain.exception.EmailAlreadyUsedException;
import com.example.auth_starter.domain.model.Role;
import com.example.auth_starter.domain.model.User;
import com.example.auth_starter.domain.port.in.RegisterUserCommand;
import com.example.auth_starter.domain.port.in.RegisterUserUseCase;
import com.example.auth_starter.domain.port.out.PasswordEncoderPort;
import com.example.auth_starter.domain.port.out.UserRepositoryPort;

@Service
public class RegisterUserUseCaseImpl implements RegisterUserUseCase {
    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;

    public RegisterUserUseCaseImpl(UserRepositoryPort userRepositoryPort, PasswordEncoderPort passwordEncoderPort) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
    }

    @Override
    public User register(RegisterUserCommand registerUserCommand) {
        var isEmailUsed = userRepositoryPort.existsByEmail(registerUserCommand.email());
        if (isEmailUsed) {
            throw new EmailAlreadyUsedException();
        }
        var id = UUID.randomUUID();
        var hashedPassword = passwordEncoderPort.hash(registerUserCommand.password());
        User newUser = new User(id, registerUserCommand.email(), hashedPassword, Set.of(Role.USER));
        return userRepositoryPort.save(newUser);
    }
}
