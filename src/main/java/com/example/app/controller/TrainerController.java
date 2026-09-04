package com.example.app.controller;

import com.example.app.dto.TrainerRequest;
import com.example.app.entity.Trainer;
import com.example.app.security.SecurityUtils;
import com.example.app.service.TrainerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
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
@RequestMapping("/api/v1/trainers")
@RequiredArgsConstructor
@Tag(name = "Trainers", description = "Trainer profile management")
public class TrainerController {

  private final TrainerService trainerService;

  @GetMapping
  @Operation(summary = "List all trainers (paginated)")
  public ResponseEntity<Page<Trainer>> findAll(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
    return ResponseEntity.ok(trainerService.findAll(PageRequest.of(page, size)));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get a trainer by id")
  public ResponseEntity<Trainer> findById(@PathVariable Long id) {
    return ResponseEntity.ok(trainerService.findById(id));
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN', 'BRANCH_MANAGER')")
  @Operation(summary = "Create a trainer profile (SUPER_ADMIN, GYM_ADMIN, BRANCH_MANAGER)")
  public ResponseEntity<Trainer> create(@Valid @RequestBody TrainerRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(trainerService.create(request));
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update a trainer profile (admin roles or the trainer themselves)")
  public ResponseEntity<Trainer> update(
      @PathVariable Long id, @Valid @RequestBody TrainerRequest request) {
    Trainer existing = trainerService.findById(id);
    if (!SecurityUtils.isAdmin()
        && !existing.getUser().getId().equals(SecurityUtils.getCurrentUserId())) {
      throw new AccessDeniedException("You may only update your own trainer profile");
    }
    return ResponseEntity.ok(trainerService.update(id, request));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN', 'BRANCH_MANAGER')")
  @Operation(summary = "Delete a trainer profile (SUPER_ADMIN, GYM_ADMIN, BRANCH_MANAGER)")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    trainerService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
