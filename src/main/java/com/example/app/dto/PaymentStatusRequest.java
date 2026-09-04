package com.example.app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PaymentStatusRequest {

  @NotBlank(message = "status is required")
  private String status;
}
