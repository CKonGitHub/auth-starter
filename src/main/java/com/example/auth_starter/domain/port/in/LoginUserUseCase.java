package com.example.auth_starter.domain.port.in;

public interface LoginUserUseCase {
    LoginUserResult login(LoginUserCommand command);
}
