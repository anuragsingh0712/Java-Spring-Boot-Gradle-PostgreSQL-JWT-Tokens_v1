package com.example.app.service;

import com.example.app.dto.ProgressTrackerRequest;
import com.example.app.entity.Member;
import com.example.app.entity.ProgressTracker;
import com.example.app.exception.BadRequestException;
import com.example.app.exception.ResourceNotFoundException;
import com.example.app.repository.ProgressTrackerRepository;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProgressTrackerService {

  private static final Set<String> VALID_STATUSES =
      Set.of("NOT_STARTED", "IN_PROGRESS", "COMPLETED");

  private final ProgressTrackerRepository progressTrackerRepository;

  private final MemberService memberService;

  public Page<ProgressTracker> findAll(Pageable pageable) {
    return progressTrackerRepository.findAll(pageable);
  }

  public Page<ProgressTracker> findByMember(Long memberId, Pageable pageable) {
    return progressTrackerRepository.findByMemberId(memberId, pageable);
  }

  public ProgressTracker findById(Long id) {
    return progressTrackerRepository
        .findById(id)
        .orElseThrow(
            () -> new ResourceNotFoundException("Progress tracker entry not found with id: " + id));
  }

  @Transactional
  public ProgressTracker create(ProgressTrackerRequest request) {
    Member member = memberService.findById(request.getMemberId());
    ProgressTracker tracker = new ProgressTracker();
    tracker.setMember(member);
    applyRequest(tracker, request);
    return progressTrackerRepository.save(tracker);
  }

  @Transactional
  public ProgressTracker update(Long id, ProgressTrackerRequest request) {
    ProgressTracker tracker = findById(id);
    Member member = memberService.findById(request.getMemberId());
    tracker.setMember(member);
    applyRequest(tracker, request);
    return progressTrackerRepository.save(tracker);
  }

  @Transactional
  public void delete(Long id) {
    ProgressTracker tracker = findById(id);
    progressTrackerRepository.delete(tracker);
  }

  private void applyRequest(ProgressTracker tracker, ProgressTrackerRequest request) {
    tracker.setTitle(request.getTitle());
    tracker.setDescription(request.getDescription());
    tracker.setProgressPercentage(request.getProgressPercentage());
    tracker.setRecordedAt(request.getRecordedAt());
    String status =
        request.getStatus() == null || request.getStatus().isBlank()
            ? "IN_PROGRESS"
            : request.getStatus().toUpperCase();
    if (!VALID_STATUSES.contains(status)) {
      throw new BadRequestException("Invalid progress tracker status: " + status);
    }
    tracker.setStatus(status);
  }
}
