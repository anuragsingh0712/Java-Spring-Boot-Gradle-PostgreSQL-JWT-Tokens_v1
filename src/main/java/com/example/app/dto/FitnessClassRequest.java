package com.example.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class FitnessClassRequest {

  @NotBlank(message = "name is required")
  private String name;

  @NotBlank(message = "type is required")
  private String type;

  @NotNull(message = "branchId is required")
  private Long branchId;

  @NotNull(message = "trainerId is required")
  private Long trainerId;

  @NotNull(message = "scheduleTime is required")
  private LocalDateTime scheduleTime;

  @Positive(message = "capacity must be positive")
  private int capacity;
}
