package com.example.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {

  private String token;

  private String refreshToken;

  private String tokenType = "Bearer";

  private Long userId;

  private String email;

  private String role;

  public JwtResponse(String token, String refreshToken, Long userId, String email, String role) {
    this.token = token;
    this.refreshToken = refreshToken;
    this.userId = userId;
    this.email = email;
    this.role = role;
  }
}
