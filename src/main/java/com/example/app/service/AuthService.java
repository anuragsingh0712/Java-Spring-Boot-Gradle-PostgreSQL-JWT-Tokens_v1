package com.example.app.service;

import com.example.app.dto.JwtResponse;
import com.example.app.dto.LoginRequest;
import com.example.app.dto.RegisterRequest;
import com.example.app.entity.RefreshToken;
import com.example.app.entity.User;
import com.example.app.exception.BadRequestException;
import com.example.app.repository.RefreshTokenRepository;
import com.example.app.repository.UserRepository;
import com.example.app.security.JwtUtil;
import io.jsonwebtoken.Claims;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

  private static final Set<String> VALID_ROLES =
      Set.of("SUPER_ADMIN", "GYM_ADMIN", "BRANCH_MANAGER", "TRAINER", "RECEPTIONIST", "MEMBER");

  private final UserRepository userRepository;

  private final RefreshTokenRepository refreshTokenRepository;

  private final PasswordEncoder passwordEncoder;

  private final JwtUtil jwtUtil;

  @Transactional
  public JwtResponse register(RegisterRequest request) {
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new BadRequestException("Email is already registered");
    }
    String role =
        request.getRole() == null || request.getRole().isBlank()
            ? "MEMBER"
            : request.getRole().toUpperCase();
    if (!VALID_ROLES.contains(role)) {
      throw new BadRequestException("Invalid role: " + role);
    }
    User user = new User();
    user.setName(request.getName());
    user.setEmail(request.getEmail());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setRole(role);
    User saved = userRepository.save(user);
    return issueTokens(saved);
  }

  public JwtResponse login(LoginRequest request) {
    User user =
        userRepository
            .findByEmail(request.getEmail())
            .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));
    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      throw new BadCredentialsException("Invalid email or password");
    }
    return issueTokens(user);
  }

  @Transactional
  public JwtResponse refresh(String refreshToken) {
    if (!jwtUtil.isTokenValid(refreshToken)) {
      throw new BadCredentialsException("Invalid or expired refresh token");
    }
    Claims claims = jwtUtil.extractAllClaims(refreshToken);
    if (!"refresh".equals(claims.get("type", String.class))) {
      throw new BadCredentialsException("Provided token is not a refresh token");
    }
    String jti = claims.get("jti", String.class);
    RefreshToken stored =
        refreshTokenRepository
            .findByJti(jti)
            .orElseThrow(() -> new BadCredentialsException("Refresh token not recognized"));
    if (stored.isRevoked()) {
      throw new BadCredentialsException("Refresh token has been revoked");
    }
    String email = jwtUtil.extractUsername(refreshToken);
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new BadCredentialsException("User no longer exists"));
    String newAccessToken =
        jwtUtil.generateAccessToken(user.getEmail(), user.getId(), user.getRole());
    return new JwtResponse(
        newAccessToken, refreshToken, user.getId(), user.getEmail(), user.getRole());
  }

  @Transactional
  public void logout(String refreshToken) {
    if (!jwtUtil.isTokenValid(refreshToken)) {
      return;
    }
    Claims claims = jwtUtil.extractAllClaims(refreshToken);
    String jti = claims.get("jti", String.class);
    refreshTokenRepository
        .findByJti(jti)
        .ifPresent(
            rt -> {
              rt.setRevoked(true);
              refreshTokenRepository.save(rt);
            });
  }

  private JwtResponse issueTokens(User user) {
    String accessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getId(), user.getRole());
    String jti = UUID.randomUUID().toString();
    String refreshToken =
        jwtUtil.generateRefreshToken(user.getEmail(), user.getId(), user.getRole(), jti);

    RefreshToken entity = new RefreshToken();
    entity.setUserId(user.getId());
    entity.setJti(jti);
    entity.setExpiresAt(
        LocalDateTime.ofInstant(
            Instant.now().plusMillis(jwtUtil.getRefreshExpiryMs()), ZoneId.systemDefault()));
    entity.setRevoked(false);
    refreshTokenRepository.save(entity);

    return new JwtResponse(
        accessToken, refreshToken, user.getId(), user.getEmail(), user.getRole());
  }
}
