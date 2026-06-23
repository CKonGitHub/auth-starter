package com.example.auth_starter.infrastructure.adapter.in.security;

import java.util.List;
import java.util.UUID;

public record AuthenticatedUserPrincipal(
        UUID userId,
        String email,
        List<String> roles) {

}