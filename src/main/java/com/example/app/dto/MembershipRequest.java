package com.example.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class MembershipRequest {

  @NotNull(message = "memberId is required")
  private Long memberId;

  @NotBlank(message = "planName is required")
  private String planName;

  @NotNull(message = "price is required")
  @Positive(message = "price must be positive")
  private BigDecimal price;

  @Positive(message = "durationDays must be positive")
  private int durationDays;
}
