package com.example.auth_starter.infrastructure.adapter.in.web;

import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.auth_starter.domain.port.in.RegisterUserCommand;
import com.example.auth_starter.domain.port.in.RegisterUserUseCase;

@RestController
@RequestMapping("/api/auth")
public class RegisterController {
    private final RegisterUserUseCase registerUserUseCase;

    public RegisterController(RegisterUserUseCase registerUserUseCase) {
        this.registerUserUseCase = registerUserUseCase;
    }

    @PostMapping("/register")
    public AuthenticatedUserResponse register(@RequestBody @Valid RegisterDTO dto) {
        var registerUserCommand = new RegisterUserCommand(dto.email(), dto.password());
        var user = registerUserUseCase.register(registerUserCommand);
        return new AuthenticatedUserResponse(
                user.getId(),
                user.getEmail(),
                user.getRoles());
    }

}
