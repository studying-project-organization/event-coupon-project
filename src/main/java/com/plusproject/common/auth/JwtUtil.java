package com.plusproject.common.auth;

import com.plusproject.common.exception.ApplicationException;
import com.plusproject.common.exception.ErrorCode;
import com.plusproject.domain.user.enums.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

public class JwtUtil {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final long ACCESS_TOKEN_EXPIRATION = 30 * 60 * 1000L;    // 30분

    @Value("${jwt.secret.key}")
    private String secretKey;

    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public JwtUtil() {
    }

    public String createAccessToken(Long userId, UserRole userRole) {
        Map<String, String> claims = Map.of(
            "userRole", userRole.name()
        );

        return BEARER_PREFIX + createToken(userId, claims, ACCESS_TOKEN_EXPIRATION);
    }

    private String createToken(Long userId, Map<String, String> claims, long expiration) {
        Instant now = Instant.now();
        Instant expiry = now.plus(expiration, ChronoUnit.MILLIS);

        return Jwts.builder()
            .subject(userId.toString())
            .claims(claims)
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiry))
            .signWith(key)
            .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    public String substringToken(String tokenValue) {
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
            return tokenValue.substring(7);
        }

        throw new ApplicationException(ErrorCode.MISSING_JWT_TOKEN);
    }
}
