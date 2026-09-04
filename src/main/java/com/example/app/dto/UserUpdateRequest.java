package com.example.app.dto;

import lombok.Data;

@Data
public class UserUpdateRequest {

  private String name;

  private String password;

  private String role;
}
