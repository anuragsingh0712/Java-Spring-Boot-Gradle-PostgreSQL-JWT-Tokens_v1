package com.example.app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GymRequest {

  @NotBlank(message = "name is required")
  private String name;

  private String description;

  private String address;
}
