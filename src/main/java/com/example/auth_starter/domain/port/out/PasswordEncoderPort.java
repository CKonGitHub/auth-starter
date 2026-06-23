package com.example.auth_starter.domain.port.out;

public interface PasswordEncoderPort {
    String hash(String password);

    boolean matches(String password, String hash);
}
