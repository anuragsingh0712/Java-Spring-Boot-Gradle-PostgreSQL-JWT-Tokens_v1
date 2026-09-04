package com.example.app.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

  private Long id;

  private String name;

  private String email;

  private String role;

  private LocalDateTime createdAt;
}
