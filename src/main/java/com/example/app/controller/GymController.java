package com.example.app.controller;

import com.example.app.dto.GymRequest;
import com.example.app.entity.Gym;
import com.example.app.service.GymService;
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
@RequestMapping("/api/v1/gyms")
@RequiredArgsConstructor
@Tag(name = "Gyms", description = "Gym management")
public class GymController {

  private final GymService gymService;

  @GetMapping
  @Operation(summary = "List all gyms (paginated)")
  public ResponseEntity<Page<Gym>> findAll(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
    return ResponseEntity.ok(gymService.findAll(PageRequest.of(page, size)));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get a gym by id")
  public ResponseEntity<Gym> findById(@PathVariable Long id) {
    return ResponseEntity.ok(gymService.findById(id));
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN')")
  @Operation(summary = "Create a gym (SUPER_ADMIN, GYM_ADMIN only)")
  public ResponseEntity<Gym> create(@Valid @RequestBody GymRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(gymService.create(request));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN')")
  @Operation(summary = "Update a gym (SUPER_ADMIN, GYM_ADMIN only)")
  public ResponseEntity<Gym> update(@PathVariable Long id, @Valid @RequestBody GymRequest request) {
    return ResponseEntity.ok(gymService.update(id, request));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('SUPER_ADMIN')")
  @Operation(summary = "Delete a gym (SUPER_ADMIN only)")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    gymService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
