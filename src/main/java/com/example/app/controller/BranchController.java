package com.example.app.controller;

import com.example.app.dto.BranchRequest;
import com.example.app.entity.Branch;
import com.example.app.service.BranchService;
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
@RequestMapping("/api/v1/branches")
@RequiredArgsConstructor
@Tag(name = "Branches", description = "Gym branch management")
public class BranchController {

  private final BranchService branchService;

  @GetMapping
  @Operation(summary = "List all branches (paginated)")
  public ResponseEntity<Page<Branch>> findAll(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
    return ResponseEntity.ok(branchService.findAll(PageRequest.of(page, size)));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get a branch by id")
  public ResponseEntity<Branch> findById(@PathVariable Long id) {
    return ResponseEntity.ok(branchService.findById(id));
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN')")
  @Operation(summary = "Create a branch (SUPER_ADMIN, GYM_ADMIN only)")
  public ResponseEntity<Branch> create(@Valid @RequestBody BranchRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(branchService.create(request));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN', 'BRANCH_MANAGER')")
  @Operation(summary = "Update a branch (SUPER_ADMIN, GYM_ADMIN, BRANCH_MANAGER)")
  public ResponseEntity<Branch> update(
      @PathVariable Long id, @Valid @RequestBody BranchRequest request) {
    return ResponseEntity.ok(branchService.update(id, request));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN')")
  @Operation(summary = "Delete a branch (SUPER_ADMIN, GYM_ADMIN only)")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    branchService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
