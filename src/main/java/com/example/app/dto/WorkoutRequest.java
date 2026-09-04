package com.example.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Data;

@Data
public class WorkoutRequest {

  @NotBlank(message = "name is required")
  private String name;

  private String description;

  @NotNull(message = "trainerId is required")
  private Long trainerId;

  @NotNull(message = "memberId is required")
  private Long memberId;

  private LocalDate scheduledDate;

  private String exercises;

  private String status;
}
