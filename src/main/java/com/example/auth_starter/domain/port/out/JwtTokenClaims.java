package com.example.auth_starter.domain.port.out;

import java.util.List;
import java.util.UUID;

public record JwtTokenClaims(
        UUID userId,
        String email,
        List<String> roles) {

}