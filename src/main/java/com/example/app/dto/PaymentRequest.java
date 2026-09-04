package com.example.app.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class PaymentRequest {

  @NotNull(message = "memberId is required")
  private Long memberId;

  private Long membershipId;

  @NotNull(message = "amount is required")
  @Positive(message = "amount must be positive")
  private BigDecimal amount;

  private String method;
}
