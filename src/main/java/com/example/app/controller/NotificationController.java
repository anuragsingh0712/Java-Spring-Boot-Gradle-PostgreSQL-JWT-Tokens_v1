package com.example.app.controller;

import com.example.app.entity.Notification;
import com.example.app.security.SecurityUtils;
import com.example.app.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(
    name = "Notifications",
    description = "User notifications for membership, payment and other events")
public class NotificationController {

  private final NotificationService notificationService;

  @GetMapping
  @Operation(summary = "List the current user's notifications (paginated)")
  public ResponseEntity<Page<Notification>> findMine(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
    Long userId = SecurityUtils.getCurrentUserId();
    return ResponseEntity.ok(notificationService.findByUser(userId, PageRequest.of(page, size)));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get a notification by id (owner only)")
  public ResponseEntity<Notification> findById(@PathVariable Long id) {
    Notification notification = notificationService.findById(id);
    assertOwner(notification);
    return ResponseEntity.ok(notification);
  }

  @PutMapping("/{id}/read")
  @Operation(summary = "Mark a notification as read (owner only)")
  public ResponseEntity<Notification> markRead(@PathVariable Long id) {
    Notification notification = notificationService.findById(id);
    assertOwner(notification);
    return ResponseEntity.ok(notificationService.markRead(id));
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Delete a notification (owner only)")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    Notification notification = notificationService.findById(id);
    assertOwner(notification);
    notificationService.delete(id);
    return ResponseEntity.noContent().build();
  }

  private void assertOwner(Notification notification) {
    if (!SecurityUtils.isAdmin()
        && !notification.getUser().getId().equals(SecurityUtils.getCurrentUserId())) {
      throw new AccessDeniedException("You may only access your own notifications");
    }
  }
}
