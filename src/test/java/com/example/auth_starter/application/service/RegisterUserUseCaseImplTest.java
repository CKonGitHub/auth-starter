package com.example.auth_starter.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.auth_starter.domain.exception.EmailAlreadyUsedException;
import com.example.auth_starter.domain.model.Role;
import com.example.auth_starter.domain.model.User;
import com.example.auth_starter.domain.port.in.RegisterUserCommand;
import com.example.auth_starter.domain.port.out.PasswordEncoderPort;
import com.example.auth_starter.domain.port.out.UserRepositoryPort;

@ExtendWith(MockitoExtension.class)
public class RegisterUserUseCaseImplTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private PasswordEncoderPort passwordEncoderPort;

    private RegisterUserUseCaseImpl registerUserUseCase;

    @BeforeEach
    void setUp() {
        registerUserUseCase = new RegisterUserUseCaseImpl(
                userRepositoryPort,
                passwordEncoderPort);
    }

    @Test
    void should_register_user_when_email_is_not_used() {
        // given
        String email = "test@example.com";
        String rawPassword = "password12345";
        String hashedPassword = "hashed-password";
        var registerUserCommand = new RegisterUserCommand(email, rawPassword);

        when(userRepositoryPort.existsByEmail(email)).thenReturn(false);
        when(passwordEncoderPort.hash(rawPassword)).thenReturn(hashedPassword);
        when(userRepositoryPort.save(org.mockito.ArgumentMatchers.any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        User result = registerUserUseCase.register(registerUserCommand);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getEmail()).isEqualTo(email);
        assertThat(result.getPassword()).isEqualTo(hashedPassword);
        assertThat(result.getRoles()).containsExactly(Role.USER);

        verify(userRepositoryPort).existsByEmail(email);
        verify(passwordEncoderPort).hash(rawPassword);
        verify(userRepositoryPort).save(org.mockito.ArgumentMatchers.any(User.class));
    }

    @Test
    void should_throw_exception_when_email_is_already_used() {
        // given
        String email = "test@example.com";
        String rawPassword = "password12345";
        var registerUserCommand = new RegisterUserCommand(email, rawPassword);
        when(userRepositoryPort.existsByEmail(email)).thenReturn(true);

        // when / then
        assertThatThrownBy(() -> registerUserUseCase.register(registerUserCommand))
                .isInstanceOf(EmailAlreadyUsedException.class)
                .hasMessage("Email déjà utilisé");

        verify(userRepositoryPort).existsByEmail(email);
        verify(passwordEncoderPort, never()).hash(rawPassword);
        verify(userRepositoryPort, never()).save(any(User.class));
    }
}