package com.example.auth_starter.domain.port.in;

import com.example.auth_starter.domain.model.User;

public interface RegisterUserUseCase {
    User register(RegisterUserCommand registerUserCommand);
}
