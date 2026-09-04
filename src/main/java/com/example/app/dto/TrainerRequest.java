package com.example.app.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TrainerRequest {

  @NotNull(message = "userId is required")
  private Long userId;

  @NotNull(message = "branchId is required")
  private Long branchId;

  private String specialization;

  private String bio;

  private String status;
}
