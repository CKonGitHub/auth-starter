package com.example.auth_starter.application.service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.auth_starter.domain.exception.InvalidCredentialsException;
import com.example.auth_starter.domain.model.Role;
import com.example.auth_starter.domain.model.User;
import com.example.auth_starter.domain.port.in.LoginUserCommand;
import com.example.auth_starter.domain.port.out.JwtTokenPort;
import com.example.auth_starter.domain.port.out.PasswordEncoderPort;
import com.example.auth_starter.domain.port.out.UserRepositoryPort;

public class LoginUserUseCaseImplTest {

    private UserRepositoryPort userRepositoryPort;
    private PasswordEncoderPort passwordEncoderPort;
    private JwtTokenPort jwtTokenPort;

    private LoginUserUseCaseImpl loginUserUseCase;

    @BeforeEach
    void setUp() {
        userRepositoryPort = mock(UserRepositoryPort.class);
        passwordEncoderPort = mock(PasswordEncoderPort.class);
        jwtTokenPort = mock(JwtTokenPort.class);

        loginUserUseCase = new LoginUserUseCaseImpl(
                userRepositoryPort,
                passwordEncoderPort,
                jwtTokenPort);
    }

    @Test
    void should_login_user_successfully() {
        var user = new User(
                UUID.randomUUID(),
                "test@example.com",
                "hashed-password",
                Set.of(Role.USER));

        var command = new LoginUserCommand("test@example.com", "password12345");

        when(userRepositoryPort.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoderPort.matches("password12345", "hashed-password"))
                .thenReturn(true);

        when(jwtTokenPort.generateToken(user))
                .thenReturn("fake-jwt-token");

        var result = loginUserUseCase.login(command);

        assertThat(result.accessToken()).isEqualTo("fake-jwt-token");
        assertThat(result.user()).isEqualTo(user);

        verify(userRepositoryPort).findByEmail("test@example.com");
        verify(passwordEncoderPort).matches("password12345", "hashed-password");
        verify(jwtTokenPort).generateToken(user);
    }

    @Test
    void should_throw_when_user_does_not_exist() {
        var command = new LoginUserCommand("test@example.com", "password12345");

        when(userRepositoryPort.findByEmail("test@example.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> loginUserUseCase.login(command))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Identifiants invalides");

        verify(passwordEncoderPort, never()).matches(any(), any());
        verify(jwtTokenPort, never()).generateToken(any());
    }

    @Test
    void should_throw_when_password_does_not_match() {
        var user = new User(
                UUID.randomUUID(),
                "test@example.com",
                "hashed-password",
                Set.of(Role.USER));

        var command = new LoginUserCommand("test@example.com", "wrong-password");

        when(userRepositoryPort.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoderPort.matches("wrong-password", "hashed-password"))
                .thenReturn(false);

        assertThatThrownBy(() -> loginUserUseCase.login(command))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Identifiants invalides");

        verify(jwtTokenPort, never()).generateToken(any());
    }
}