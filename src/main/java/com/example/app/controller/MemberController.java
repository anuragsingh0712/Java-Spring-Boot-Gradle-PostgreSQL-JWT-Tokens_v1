package com.example.app.controller;

import com.example.app.dto.MemberRequest;
import com.example.app.entity.Member;
import com.example.app.security.SecurityUtils;
import com.example.app.service.MemberService;
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
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
@Tag(name = "Members", description = "Gym member profile management")
public class MemberController {

  private final MemberService memberService;

  @GetMapping
  @PreAuthorize(
      "hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN', 'BRANCH_MANAGER', 'RECEPTIONIST', 'TRAINER')")
  @Operation(summary = "List all members (staff only, paginated)")
  public ResponseEntity<Page<Member>> findAll(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
    return ResponseEntity.ok(memberService.findAll(PageRequest.of(page, size)));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get a member by id (staff or the member themselves)")
  public ResponseEntity<Member> findById(@PathVariable Long id) {
    Member member = memberService.findById(id);
    assertStaffOrOwner(member);
    return ResponseEntity.ok(member);
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN', 'BRANCH_MANAGER', 'RECEPTIONIST')")
  @Operation(summary = "Create a member profile (staff only)")
  public ResponseEntity<Member> create(@Valid @RequestBody MemberRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(memberService.create(request));
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update a member profile (staff or the member themselves)")
  public ResponseEntity<Member> update(
      @PathVariable Long id, @Valid @RequestBody MemberRequest request) {
    Member existing = memberService.findById(id);
    assertStaffOrOwner(existing);
    return ResponseEntity.ok(memberService.update(id, request));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN', 'BRANCH_MANAGER')")
  @Operation(summary = "Delete a member profile (SUPER_ADMIN, GYM_ADMIN, BRANCH_MANAGER)")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    memberService.delete(id);
    return ResponseEntity.noContent().build();
  }

  private void assertStaffOrOwner(Member member) {
    if (SecurityUtils.isStaff()) {
      return;
    }
    Long currentUserId = SecurityUtils.getCurrentUserId();
    if (member.getUser() == null || !member.getUser().getId().equals(currentUserId)) {
      throw new AccessDeniedException("You may only access your own member profile");
    }
  }
}
