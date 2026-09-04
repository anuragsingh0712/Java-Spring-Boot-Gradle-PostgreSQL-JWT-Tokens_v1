package com.example.app.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

  private LocalDateTime timestamp = LocalDateTime.now();

  private int status;

  private String message;

  public ErrorResponse(int status, String message) {
    this.status = status;
    this.message = message;
  }
}
