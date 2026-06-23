package com.example.auth_starter.domain.port.in;

import com.example.auth_starter.domain.model.User;

public record LoginUserResult(
        String accessToken,
        User user) {

}