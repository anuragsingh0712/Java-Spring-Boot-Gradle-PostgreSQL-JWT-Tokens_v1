package com.example.app.controller;

import com.example.app.dto.MembershipRequest;
import com.example.app.entity.Membership;
import com.example.app.service.MembershipService;
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
@RequestMapping("/api/v1/memberships")
@RequiredArgsConstructor
@Tag(name = "Memberships", description = "Membership plans and lifecycle")
public class MembershipController {

  private final MembershipService membershipService;

  @GetMapping
  @Operation(summary = "List all memberships (paginated)")
  public ResponseEntity<Page<Membership>> findAll(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
    return ResponseEntity.ok(membershipService.findAll(PageRequest.of(page, size)));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get a membership by id")
  public ResponseEntity<Membership> findById(@PathVariable Long id) {
    return ResponseEntity.ok(membershipService.findById(id));
  }

  @PostMapping
  @Operation(summary = "Purchase a new membership plan for a member")
  public ResponseEntity<Membership> purchase(@Valid @RequestBody MembershipRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(membershipService.purchase(request));
  }

  @PutMapping("/{id}/activate")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN', 'BRANCH_MANAGER', 'RECEPTIONIST')")
  @Operation(summary = "Activate a pending membership (staff only)")
  public ResponseEntity<Membership> activate(@PathVariable Long id) {
    return ResponseEntity.ok(membershipService.activate(id));
  }

  @PutMapping("/{id}/renew")
  @Operation(summary = "Renew an active or expired membership")
  public ResponseEntity<Membership> renew(@PathVariable Long id) {
    return ResponseEntity.ok(membershipService.renew(id));
  }

  @PutMapping("/{id}/cancel")
  @Operation(summary = "Cancel a membership")
  public ResponseEntity<Membership> cancel(@PathVariable Long id) {
    return ResponseEntity.ok(membershipService.cancel(id));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN')")
  @Operation(summary = "Delete a membership (SUPER_ADMIN, GYM_ADMIN only)")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    membershipService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
