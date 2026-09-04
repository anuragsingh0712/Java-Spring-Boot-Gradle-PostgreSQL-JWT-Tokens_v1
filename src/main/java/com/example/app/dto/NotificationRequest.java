package com.example.app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NotificationRequest {

  @NotBlank(message = "type is required")
  private String type;

  @NotBlank(message = "message is required")
  private String message;

  /** Optional link to a specific progress tracker entry this notification relates to. */
  private Long progressTrackerId;
}
