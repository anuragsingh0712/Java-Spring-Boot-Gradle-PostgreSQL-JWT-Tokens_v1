package com.example.app.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Data;

@Data
public class ProgressTrackerRequest {

  @NotNull(message = "memberId is required")
  private Long memberId;

  @NotBlank(message = "title is required")
  private String title;

  private String description;

  @NotNull(message = "progressPercentage is required")
  @Min(value = 0, message = "progressPercentage must be between 0 and 100")
  @Max(value = 100, message = "progressPercentage must be between 0 and 100")
  private Integer progressPercentage;

  private String status;

  private LocalDate recordedAt;
}
