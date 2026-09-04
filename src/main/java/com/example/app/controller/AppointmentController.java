package com.example.app.controller;

import com.example.app.dto.AppointmentRequest;
import com.example.app.dto.PaymentStatusRequest;
import com.example.app.entity.Appointment;
import com.example.app.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
@Tag(name = "Appointments", description = "Personal training appointment scheduling")
public class AppointmentController {

  private final AppointmentService appointmentService;

  @GetMapping
  @Operation(summary = "List all appointments (paginated)")
  public ResponseEntity<Page<Appointment>> findAll(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
    return ResponseEntity.ok(appointmentService.findAll(PageRequest.of(page, size)));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get an appointment by id")
  public ResponseEntity<Appointment> findById(@PathVariable Long id) {
    return ResponseEntity.ok(appointmentService.findById(id));
  }

  @PostMapping
  @Operation(summary = "Book a personal training appointment (prevents trainer conflicts)")
  public ResponseEntity<Appointment> create(@Valid @RequestBody AppointmentRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.create(request));
  }

  @PutMapping("/{id}")
  @Operation(summary = "Reschedule an appointment (prevents trainer conflicts)")
  public ResponseEntity<Appointment> update(
      @PathVariable Long id, @Valid @RequestBody AppointmentRequest request) {
    return ResponseEntity.ok(appointmentService.update(id, request));
  }

  @PutMapping("/{id}/status")
  @Operation(summary = "Update an appointment's status")
  public ResponseEntity<Appointment> updateStatus(
      @PathVariable Long id, @Valid @RequestBody PaymentStatusRequest request) {
    return ResponseEntity.ok(appointmentService.updateStatus(id, request.getStatus()));
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Delete an appointment")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    appointmentService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
