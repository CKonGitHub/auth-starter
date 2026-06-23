package com.example.auth_starter.domain.exception;

public class EmailAlreadyUsedException extends RuntimeException {
    public EmailAlreadyUsedException() {
        super("Email déjà utilisé");
    }
}
