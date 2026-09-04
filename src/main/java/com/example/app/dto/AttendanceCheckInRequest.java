package com.example.app.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AttendanceCheckInRequest {

  @NotNull(message = "memberId is required")
  private Long memberId;
}
