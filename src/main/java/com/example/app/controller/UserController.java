package com.example.app.controller;

import com.example.app.dto.UserDto;
import com.example.app.dto.UserUpdateRequest;
import com.example.app.security.SecurityUtils;
import com.example.app.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User account management")
public class UserController {

  private final UserService userService;

  @GetMapping
  @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN')")
  @Operation(summary = "List all users (admin only, paginated)")
  public ResponseEntity<Page<UserDto>> findAll(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
    Pageable pageable = PageRequest.of(page, size);
    return ResponseEntity.ok(userService.findAll(pageable));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get a user by id (self or admin)")
  public ResponseEntity<UserDto> findById(@PathVariable Long id) {
    assertSelfOrAdmin(id);
    return ResponseEntity.ok(userService.findById(id));
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update a user (self or admin)")
  public ResponseEntity<UserDto> update(
      @PathVariable Long id, @RequestBody UserUpdateRequest request) {
    assertSelfOrAdmin(id);
    if (!SecurityUtils.isAdmin() && request.getRole() != null) {
      throw new AccessDeniedException("Only admins may change roles");
    }
    return ResponseEntity.ok(userService.update(id, request));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN')")
  @Operation(summary = "Delete a user (admin only)")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    userService.delete(id);
    return ResponseEntity.noContent().build();
  }

  private void assertSelfOrAdmin(Long id) {
    Long currentUserId = SecurityUtils.getCurrentUserId();
    if (!SecurityUtils.isAdmin() && !id.equals(currentUserId)) {
      throw new AccessDeniedException("You may only access your own account");
    }
  }
}
