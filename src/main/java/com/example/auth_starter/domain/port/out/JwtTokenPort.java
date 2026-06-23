package com.example.auth_starter.domain.port.out;

import com.example.auth_starter.domain.model.User;

public interface JwtTokenPort {

    String generateToken(User user);

    JwtTokenClaims validateToken(String token);
}