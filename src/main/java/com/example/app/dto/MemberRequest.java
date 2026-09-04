package com.example.app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Data;

@Data
public class MemberRequest {

  private Long userId;

  @NotNull(message = "branchId is required")
  private Long branchId;

  @NotBlank(message = "firstName is required")
  private String firstName;

  @NotBlank(message = "lastName is required")
  private String lastName;

  @NotBlank(message = "email is required")
  @Email(message = "email must be valid")
  private String email;

  private String phone;

  private LocalDate dateOfBirth;

  private String status;

  private LocalDate joinDate;
}
