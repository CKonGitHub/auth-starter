package com.example.auth_starter.infrastructure.adapter.in.web;

public record LoginResponse(
                String accessToken,
                String tokenType,
                AuthenticatedUserResponse user) {

}