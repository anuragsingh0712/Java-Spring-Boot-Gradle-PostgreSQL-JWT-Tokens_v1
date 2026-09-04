package com.example.app.service;

import com.example.app.dto.WorkoutRequest;
import com.example.app.entity.Member;
import com.example.app.entity.Trainer;
import com.example.app.entity.Workout;
import com.example.app.exception.BadRequestException;
import com.example.app.exception.ResourceNotFoundException;
import com.example.app.repository.WorkoutRepository;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WorkoutService {

  private static final Set<String> VALID_STATUSES = Set.of("ASSIGNED", "IN_PROGRESS", "COMPLETED");

  private final WorkoutRepository workoutRepository;

  private final TrainerService trainerService;

  private final MemberService memberService;

  public Page<Workout> findAll(Pageable pageable) {
    return workoutRepository.findAll(pageable);
  }

  public Workout findById(Long id) {
    return workoutRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Workout not found with id: " + id));
  }

  @Transactional
  public Workout create(WorkoutRequest request) {
    Trainer trainer = trainerService.findById(request.getTrainerId());
    Member member = memberService.findById(request.getMemberId());
    Workout workout = new Workout();
    workout.setTrainer(trainer);
    workout.setMember(member);
    applyRequest(workout, request);
    return workoutRepository.save(workout);
  }

  @Transactional
  public Workout update(Long id, WorkoutRequest request) {
    Workout workout = findById(id);
    Trainer trainer = trainerService.findById(request.getTrainerId());
    Member member = memberService.findById(request.getMemberId());
    workout.setTrainer(trainer);
    workout.setMember(member);
    applyRequest(workout, request);
    return workoutRepository.save(workout);
  }

  @Transactional
  public void delete(Long id) {
    Workout workout = findById(id);
    workoutRepository.delete(workout);
  }

  private void applyRequest(Workout workout, WorkoutRequest request) {
    workout.setName(request.getName());
    workout.setDescription(request.getDescription());
    workout.setScheduledDate(request.getScheduledDate());
    workout.setExercises(request.getExercises());
    String status =
        request.getStatus() == null || request.getStatus().isBlank()
            ? "ASSIGNED"
            : request.getStatus().toUpperCase();
    if (!VALID_STATUSES.contains(status)) {
      throw new BadRequestException("Invalid workout status: " + status);
    }
    workout.setStatus(status);
  }
}
