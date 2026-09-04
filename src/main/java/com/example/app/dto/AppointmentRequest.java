package com.example.app.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class AppointmentRequest {

  @NotNull(message = "memberId is required")
  private Long memberId;

  @NotNull(message = "trainerId is required")
  private Long trainerId;

  @NotNull(message = "scheduledAt is required")
  private LocalDateTime scheduledAt;

  private int durationMinutes;
}
