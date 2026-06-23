package com.example.auth_starter.infrastructure.adapter.in.web;

import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.auth_starter.domain.model.Role;
import com.example.auth_starter.infrastructure.adapter.in.security.AuthenticatedUserPrincipal;

@RestController
@RequestMapping("/api/auth")
public class MeController {

    @GetMapping("/me")
    public AuthenticatedUserResponse me(Authentication authentication) {
        var principal = (AuthenticatedUserPrincipal) authentication.getPrincipal();

        var roles = principal.roles()
                .stream()
                .map(Role::valueOf)
                .collect(Collectors.toSet());

        return new AuthenticatedUserResponse(
                principal.userId(),
                principal.email(),
                roles);
    }
}