package com.techblog.backend.security;

import com.techblog.backend.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    
    private final JwtProperties jwtProperties;
    private SecretKey secretKey;
    
    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(
            jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8)
        );
    }
    
    public String generateToken(String username, String role) {
        Instant now = Instant.now();
        Instant expiration = now.plus(jwtProperties.getAccessTtlMinutes(), ChronoUnit.MINUTES);
        
        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .issuer(jwtProperties.getIssuer())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .id(UUID.randomUUID().toString())
                .signWith(secretKey)
                .compact();
    }
    
    public Claims validateToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    public String getUsernameFromToken(String token) {
        return validateToken(token).getSubject();
    }
    
    public String getRoleFromToken(String token) {
        return validateToken(token).get("role", String.class);
    }
}
