package com.example.app.service;

import com.example.app.dto.NotificationRequest;
import com.example.app.entity.Notification;
import com.example.app.entity.ProgressTracker;
import com.example.app.entity.User;
import com.example.app.exception.ResourceNotFoundException;
import com.example.app.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

  private final NotificationRepository notificationRepository;

  private final SimpMessagingTemplate messagingTemplate;

  private final ProgressTrackerService progressTrackerService;

  @Transactional
  public Notification create(User user, String type, String message) {
    return create(user, type, message, null);
  }

  @Transactional
  public Notification create(
      User user, String type, String message, ProgressTracker progressTracker) {
    Notification notification = new Notification();
    notification.setUser(user);
    notification.setType(type);
    notification.setMessage(message);
    notification.setRead(false);
    notification.setProgressTracker(progressTracker);
    Notification saved = notificationRepository.save(notification);
    try {
      messagingTemplate.convertAndSend("/topic/notifications/" + user.getId(), saved);
    } catch (Exception ignored) {
      // websocket broadcast is best-effort and must not break the main flow
    }
    return saved;
  }

  @Transactional
  public Notification createForUser(User user, NotificationRequest request) {
    ProgressTracker progressTracker = null;
    if (request.getProgressTrackerId() != null) {
      progressTracker = progressTrackerService.findById(request.getProgressTrackerId());
    }
    return create(user, request.getType(), request.getMessage(), progressTracker);
  }

  public Page<Notification> findByUser(Long userId, Pageable pageable) {
    return notificationRepository.findByUserId(userId, pageable);
  }

  public Notification findById(Long id) {
    return notificationRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));
  }

  @Transactional
  public Notification markRead(Long id) {
    Notification notification = findById(id);
    notification.setRead(true);
    return notificationRepository.save(notification);
  }

  @Transactional
  public void delete(Long id) {
    Notification notification = findById(id);
    notificationRepository.delete(notification);
  }
}
