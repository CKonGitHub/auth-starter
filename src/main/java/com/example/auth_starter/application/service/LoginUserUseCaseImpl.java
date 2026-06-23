package com.example.auth_starter.application.service;

import org.springframework.stereotype.Service;

import com.example.auth_starter.domain.exception.InvalidCredentialsException;
import com.example.auth_starter.domain.port.in.LoginUserCommand;
import com.example.auth_starter.domain.port.in.LoginUserResult;
import com.example.auth_starter.domain.port.in.LoginUserUseCase;
import com.example.auth_starter.domain.port.out.JwtTokenPort;
import com.example.auth_starter.domain.port.out.PasswordEncoderPort;
import com.example.auth_starter.domain.port.out.UserRepositoryPort;

@Service
public class LoginUserUseCaseImpl implements LoginUserUseCase {
        private final UserRepositoryPort userRepositoryPort;
        private final PasswordEncoderPort passwordEncoderPort;
        private final JwtTokenPort jwtTokenPort;

        public LoginUserUseCaseImpl(UserRepositoryPort userRepositoryPort, PasswordEncoderPort passwordEncoderPort,
                        JwtTokenPort jwtTokenPort) {
                this.userRepositoryPort = userRepositoryPort;
                this.passwordEncoderPort = passwordEncoderPort;
                this.jwtTokenPort = jwtTokenPort;
        }

        @Override
        public LoginUserResult login(LoginUserCommand loginUserCommand) {
                var user = userRepositoryPort.findByEmail(loginUserCommand.email())
                                .orElseThrow(() -> new InvalidCredentialsException());
                var isMatching = passwordEncoderPort.matches(loginUserCommand.password(), user.getPassword());

                if (!isMatching) {
                        throw new InvalidCredentialsException();
                }

                var token = jwtTokenPort.generateToken(user);

                return new LoginUserResult(token, user);
        }

}
