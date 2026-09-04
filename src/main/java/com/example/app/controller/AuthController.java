package com.example.app.controller;

import com.example.app.dto.JwtResponse;
import com.example.app.dto.LoginRequest;
import com.example.app.dto.RefreshRequest;
import com.example.app.dto.RegisterRequest;
import com.example.app.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Registration, login, token refresh and logout")
public class AuthController {

  private final AuthService authService;

  @PostMapping("/register")
  @Operation(summary = "Register a new user account")
  public ResponseEntity<JwtResponse> register(@Valid @RequestBody RegisterRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
  }

  @PostMapping("/login")
  @Operation(summary = "Login with email and password")
  public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
    return ResponseEntity.ok(authService.login(request));
  }

  @PostMapping("/refresh")
  @Operation(summary = "Exchange a valid refresh token for a new access token")
  public ResponseEntity<JwtResponse> refresh(@Valid @RequestBody RefreshRequest request) {
    return ResponseEntity.ok(authService.refresh(request.getRefreshToken()));
  }

  @PostMapping("/logout")
  @Operation(summary = "Revoke a refresh token")
  public ResponseEntity<Void> logout(@Valid @RequestBody RefreshRequest request) {
    authService.logout(request.getRefreshToken());
    return ResponseEntity.noContent().build();
  }
}
