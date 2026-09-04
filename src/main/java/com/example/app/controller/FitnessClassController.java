package com.example.app.controller;

import com.example.app.dto.FitnessClassRequest;
import com.example.app.entity.ClassRegistration;
import com.example.app.entity.FitnessClass;
import com.example.app.service.FitnessClassService;
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
@RequestMapping("/api/v1/classes")
@RequiredArgsConstructor
@Tag(name = "Fitness Classes", description = "Fitness class scheduling and registrations")
public class FitnessClassController {

  private final FitnessClassService fitnessClassService;

  @GetMapping
  @Operation(summary = "List all fitness classes (paginated)")
  public ResponseEntity<Page<FitnessClass>> findAll(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
    return ResponseEntity.ok(fitnessClassService.findAll(PageRequest.of(page, size)));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get a fitness class by id")
  public ResponseEntity<FitnessClass> findById(@PathVariable Long id) {
    return ResponseEntity.ok(fitnessClassService.findById(id));
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN', 'BRANCH_MANAGER', 'TRAINER')")
  @Operation(summary = "Create a fitness class (trainer or admin roles)")
  public ResponseEntity<FitnessClass> create(@Valid @RequestBody FitnessClassRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(fitnessClassService.create(request));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN', 'BRANCH_MANAGER', 'TRAINER')")
  @Operation(summary = "Update a fitness class (trainer or admin roles)")
  public ResponseEntity<FitnessClass> update(
      @PathVariable Long id, @Valid @RequestBody FitnessClassRequest request) {
    return ResponseEntity.ok(fitnessClassService.update(id, request));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN', 'BRANCH_MANAGER')")
  @Operation(summary = "Delete a fitness class (admin roles only)")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    fitnessClassService.delete(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/{id}/register")
  @Operation(summary = "Register a member for a fitness class (enforces capacity)")
  public ResponseEntity<ClassRegistration> register(
      @PathVariable Long id, @RequestParam Long memberId) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(fitnessClassService.register(id, memberId));
  }

  @DeleteMapping("/{id}/register")
  @Operation(summary = "Cancel a member's registration for a fitness class")
  public ResponseEntity<Void> cancelRegistration(
      @PathVariable Long id, @RequestParam Long memberId) {
    fitnessClassService.cancelRegistration(id, memberId);
    return ResponseEntity.noContent().build();
  }
}
