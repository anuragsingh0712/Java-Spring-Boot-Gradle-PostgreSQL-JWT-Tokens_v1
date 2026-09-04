package com.example.app.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.access-expiry-ms}")
  private long accessExpiryMs;

  @Value("${jwt.refresh-expiry-ms}")
  private long refreshExpiryMs;

  private SecretKey getSigningKey() {
    byte[] keyBytes;
    try {
      keyBytes = java.util.Base64.getDecoder().decode(secret);
    } catch (IllegalArgumentException ex) {
      keyBytes = secret.getBytes(StandardCharsets.UTF_8);
    }
    if (keyBytes.length < 32) {
      keyBytes = (secret + secret + secret).getBytes(StandardCharsets.UTF_8);
    }
    return Keys.hmacShaKeyFor(keyBytes);
  }

  public String generateAccessToken(String email, Long userId, String role) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("type", "access");
    claims.put("userId", userId);
    claims.put("role", role);
    return buildToken(claims, email, accessExpiryMs);
  }

  public String generateRefreshToken(String email, Long userId, String role, String jti) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("type", "refresh");
    claims.put("userId", userId);
    claims.put("role", role);
    claims.put("jti", jti);
    return buildToken(claims, email, refreshExpiryMs);
  }

  private String buildToken(Map<String, Object> claims, String subject, long expiryMs) {
    Date now = new Date();
    Date expiry = new Date(now.getTime() + expiryMs);
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(subject)
        .setIssuedAt(now)
        .setExpiration(expiry)
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  public Claims extractAllClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  public String extractUsername(String token) {
    return extractAllClaims(token).getSubject();
  }

  public String extractType(String token) {
    return extractAllClaims(token).get("type", String.class);
  }

  public String extractJti(String token) {
    return extractAllClaims(token).get("jti", String.class);
  }

  public boolean isTokenValid(String token) {
    try {
      Claims claims = extractAllClaims(token);
      return claims.getExpiration().after(new Date());
    } catch (JwtException | IllegalArgumentException ex) {
      return false;
    }
  }

  public long getRefreshExpiryMs() {
    return refreshExpiryMs;
  }
}
