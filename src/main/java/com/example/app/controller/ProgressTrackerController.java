package com.example.app.controller;

import com.example.app.dto.ProgressTrackerRequest;
import com.example.app.entity.ProgressTracker;
import com.example.app.service.ProgressTrackerService;
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
@RequestMapping("/api/v1/progress-trackers")
@RequiredArgsConstructor
@Tag(name = "Progress Trackers", description = "Track member fitness progress entries")
public class ProgressTrackerController {

  private final ProgressTrackerService progressTrackerService;

  @GetMapping
  @Operation(summary = "List all progress tracker entries (paginated)")
  public ResponseEntity<Page<ProgressTracker>> findAll(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
    return ResponseEntity.ok(progressTrackerService.findAll(PageRequest.of(page, size)));
  }

  @GetMapping("/member/{memberId}")
  @Operation(summary = "List progress tracker entries for a specific member (paginated)")
  public ResponseEntity<Page<ProgressTracker>> findByMember(
      @PathVariable Long memberId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    return ResponseEntity.ok(
        progressTrackerService.findByMember(memberId, PageRequest.of(page, size)));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get a progress tracker entry by id")
  public ResponseEntity<ProgressTracker> findById(@PathVariable Long id) {
    return ResponseEntity.ok(progressTrackerService.findById(id));
  }

  @PostMapping
  @Operation(summary = "Track a new progress entry for a member")
  public ResponseEntity<ProgressTracker> create(
      @Valid @RequestBody ProgressTrackerRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(progressTrackerService.create(request));
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update a progress tracker entry")
  public ResponseEntity<ProgressTracker> update(
      @PathVariable Long id, @Valid @RequestBody ProgressTrackerRequest request) {
    return ResponseEntity.ok(progressTrackerService.update(id, request));
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Delete a progress tracker entry")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    progressTrackerService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
