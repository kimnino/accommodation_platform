package com.accommodation.platform.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Slf4j
@Component
@Profile("!test")
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long expirationMillis;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expirationMillis
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMillis = expirationMillis;
    }

    public String generateToken(String role, Long partnerId, Long memberId) {
        Instant now = Instant.now();
        Instant expiration = now.plusMillis(expirationMillis);

        var builder = Jwts.builder()
                .subject(resolveSubject(role, partnerId, memberId))
                .claim("role", role)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration));

        if (partnerId != null) {
            builder.claim("partnerId", partnerId);
        }
        if (memberId != null) {
            builder.claim("memberId", memberId);
        }

        return builder.signWith(secretKey).compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("JWT token expired: {}", e.getMessage());
        } catch (JwtException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
        }
        return false;
    }

    public String getRole(Claims claims) {
        return claims.get("role", String.class);
    }

    public Long getPartnerId(Claims claims) {
        return claims.get("partnerId", Long.class);
    }

    public Long getMemberId(Claims claims) {
        return claims.get("memberId", Long.class);
    }

    public long getExpirationSeconds() {
        return expirationMillis / 1000;
    }

    private String resolveSubject(String role, Long partnerId, Long memberId) {
        return switch (role) {
            case "PARTNER" -> partnerId != null ? "partner:" + partnerId : "partner:unknown";
            case "CUSTOMER" -> memberId != null ? "member:" + memberId : "member:unknown";
            case "ADMIN" -> "admin";
            default -> "unknown";
        };
    }
}
