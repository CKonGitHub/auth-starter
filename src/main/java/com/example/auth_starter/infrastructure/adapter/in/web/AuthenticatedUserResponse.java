package com.example.auth_starter.infrastructure.adapter.in.web;

import java.util.Set;
import java.util.UUID;

import com.example.auth_starter.domain.model.Role;

public record AuthenticatedUserResponse(UUID id, String email, Set<Role> roles) {

}