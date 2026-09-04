package com.example.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import lombok.Data;

@Data
public class BranchRequest {

  @NotNull(message = "gymId is required")
  private Long gymId;

  @NotBlank(message = "name is required")
  private String name;

  private String address;

  private LocalTime openingTime;

  private LocalTime closingTime;

  private String facilities;

  private Long managerId;
}
