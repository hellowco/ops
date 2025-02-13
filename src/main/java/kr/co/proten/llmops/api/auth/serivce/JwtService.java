package kr.co.proten.llmops.api.auth.serivce;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private static final long ACCESS_TOKEN_EXPIRATION_MINUTES = 120; // 120분
    private static final long REFRESH_TOKEN_EXPIRATION_DAYS = 7; // 7일

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    /**
     * 🔹 로그인 시 발급하는 기본 Access Token (워크스페이스 정보 없음)
     */
    public String generateBasicAccessToken(String userId, String username, String role) {
        Instant now = Instant.now();
        Instant expirationTime = now.plus(ACCESS_TOKEN_EXPIRATION_MINUTES, ChronoUnit.MINUTES);

        return Jwts.builder()
                .subject(userId)
                .claim("username", username)
                .claim("role", role)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expirationTime))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 🔹 사용자가 워크스페이스를 선택한 후 발급하는 Access Token (워크스페이스 정보 포함)
     */
    public String generateWorkspaceAccessToken(String userId, String username, String role, String workspaceId, String workspaceRole) {
        Instant now = Instant.now();
        Instant expirationTime = now.plus(ACCESS_TOKEN_EXPIRATION_MINUTES, ChronoUnit.MINUTES);

        return Jwts.builder()
                .subject(userId)
                .claim("username", username)
                .claim("role", role)
                .claim("workspaceId", workspaceId)
                .claim("workspaceRole", workspaceRole)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expirationTime))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 🔹 Refresh Token 생성 (7일 유효)
     */
    public String generateRefreshToken(String userId) {
        Instant now = Instant.now();
        Instant expirationTime = now.plus(REFRESH_TOKEN_EXPIRATION_DAYS, ChronoUnit.DAYS);

        return Jwts.builder()
                .subject(userId)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expirationTime))
                .signWith(getSigningKey())
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public String extractUserId(String token) {
        return extractClaims(token).getSubject();
    }
}
