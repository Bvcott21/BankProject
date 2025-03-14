package com.bvcott.accountservice.util;

import io.jsonwebtoken.security.Keys;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import javax.crypto.SecretKey;

@Component
public class JwtUtil {

    // Secure key generation for HS256 (32-byte minimum key length required)
    private static final String SECRET_KEY = "your256bitsecretkeyyour256bitsecretkeyyour256bitsecretkey";
    private static final SecretKey SIGNING_KEY = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));

    public String generateToken(String username) {
        Instant now = Instant.now();

        String token =  Jwts.builder()
                .subject(username)                          // Replaces setSubject(...)
                .issuedAt(Date.from(now))                   // Replaces setIssuedAt(...)
                .expiration(Date.from(now.plus(10, ChronoUnit.HOURS))) // Replaces setExpiration(...)
                .signWith(SIGNING_KEY)                      // Let JJWT derive the algorithm
                .compact();
        return "Bearer " + token;
    }

    public String generateRefreshToken(String username) {
        Instant now = Instant.now();
        return Jwts
                .builder()
                .subject(username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(7, ChronoUnit.DAYS)))
                .signWith(SIGNING_KEY)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser()
            .verifyWith(SIGNING_KEY)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
    }

    public boolean validateToken(String token, String username) {
        String extractedUsername = extractUsername(token);
        return extractedUsername.equals(username) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        Date expiration = Jwts.parser()
                .verifyWith(SIGNING_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
        return expiration.before(new Date());
    }
}