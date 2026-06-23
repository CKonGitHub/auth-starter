package com.example.auth_starter.infrastructure.adapter.out.security;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.auth_starter.domain.model.User;
import com.example.auth_starter.domain.port.out.JwtTokenClaims;
import com.example.auth_starter.domain.port.out.JwtTokenPort;

@Component
public class JwtTokenAdapter implements JwtTokenPort {

    private final String secret;
    private final long expirationSeconds;

    public JwtTokenAdapter(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-seconds}") long expirationSeconds) {
        this.secret = secret;
        this.expirationSeconds = expirationSeconds;
    }

    @Override
    public String generateToken(User user) {
        var now = Instant.now();
        var expiresAt = now.plusSeconds(expirationSeconds);

        var roles = user.getRoles()
                .stream()
                .map(Enum::name)
                .toList();

        return JWT.create()
                .withSubject(user.getId().toString())
                .withClaim("email", user.getEmail())
                .withClaim("roles", roles)
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(expiresAt))
                .sign(Algorithm.HMAC256(secret));
    }

    @Override
    public JwtTokenClaims validateToken(String token) {
        var algorithm = Algorithm.HMAC256(secret);

        var verifier = JWT.require(algorithm)
                .build();

        var decodedJWT = verifier.verify(token);

        var userId = UUID.fromString(decodedJWT.getSubject());
        var email = decodedJWT.getClaim("email").asString();
        var roles = decodedJWT.getClaim("roles").asList(String.class);

        return new JwtTokenClaims(
                userId,
                email,
                roles);
    }
}