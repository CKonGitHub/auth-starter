package com.example.auth_starter.infrastructure.adapter.in.web;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.auth_starter.domain.port.in.LoginUserCommand;
import com.example.auth_starter.domain.port.in.LoginUserUseCase;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class LoginController {
    private final LoginUserUseCase loginUserUseCase;

    public LoginController(LoginUserUseCase loginUserUseCase) {
        this.loginUserUseCase = loginUserUseCase;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Valid LoginRequest loginRequest) {
        var result = loginUserUseCase.login(
                new LoginUserCommand(loginRequest.email(), loginRequest.password()));

        var user = result.user();

        return new LoginResponse(
                result.accessToken(),
                "Bearer",
                new AuthenticatedUserResponse(
                        user.getId(),
                        user.getEmail(),
                        user.getRoles()));
    }
}
