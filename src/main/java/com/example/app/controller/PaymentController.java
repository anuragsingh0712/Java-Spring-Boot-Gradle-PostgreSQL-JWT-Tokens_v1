package com.example.app.controller;

import com.example.app.dto.PaymentRequest;
import com.example.app.dto.PaymentStatusRequest;
import com.example.app.entity.Payment;
import com.example.app.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Payments and billing transactions")
public class PaymentController {

  private final PaymentService paymentService;

  @GetMapping
  @Operation(summary = "List all payments (paginated)")
  public ResponseEntity<Page<Payment>> findAll(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
    return ResponseEntity.ok(paymentService.findAll(PageRequest.of(page, size)));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get a payment by id")
  public ResponseEntity<Payment> findById(@PathVariable Long id) {
    return ResponseEntity.ok(paymentService.findById(id));
  }

  @PostMapping
  @Operation(summary = "Create a payment transaction")
  public ResponseEntity<Payment> create(@Valid @RequestBody PaymentRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.create(request));
  }

  @PutMapping("/{id}/status")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN', 'BRANCH_MANAGER', 'RECEPTIONIST')")
  @Operation(summary = "Update a payment's status (staff only, triggers a notification)")
  public ResponseEntity<Payment> updateStatus(
      @PathVariable Long id, @Valid @RequestBody PaymentStatusRequest request) {
    return ResponseEntity.ok(paymentService.updateStatus(id, request.getStatus()));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN')")
  @Operation(summary = "Delete a payment record (SUPER_ADMIN, GYM_ADMIN only)")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    paymentService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
