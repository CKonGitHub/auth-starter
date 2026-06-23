package com.example.auth_starter.infrastructure.adapter.in.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterDTO(@Email @NotBlank String email, @NotBlank @Size(min = 12) String password) {

}
