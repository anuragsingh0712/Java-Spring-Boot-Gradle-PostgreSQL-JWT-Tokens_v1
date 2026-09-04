package com.example.app.service;

import com.example.app.dto.TrainerRequest;
import com.example.app.entity.Branch;
import com.example.app.entity.Trainer;
import com.example.app.entity.User;
import com.example.app.exception.BadRequestException;
import com.example.app.exception.ResourceNotFoundException;
import com.example.app.repository.TrainerRepository;
import com.example.app.repository.UserRepository;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TrainerService {

  private static final Set<String> VALID_STATUSES = Set.of("ACTIVE", "ON_LEAVE", "INACTIVE");

  private final TrainerRepository trainerRepository;

  private final UserRepository userRepository;

  private final BranchService branchService;

  private final CascadeDeleteService cascadeDeleteService;

  public Page<Trainer> findAll(Pageable pageable) {
    return trainerRepository.findAll(pageable);
  }

  public Trainer findById(Long id) {
    return trainerRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with id: " + id));
  }

  public Trainer findByUserId(Long userId) {
    return trainerRepository
        .findByUserId(userId)
        .orElseThrow(
            () -> new ResourceNotFoundException("Trainer profile not found for current user"));
  }

  @Transactional
  public Trainer create(TrainerRequest request) {
    User user =
        userRepository
            .findById(request.getUserId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "User not found with id: " + request.getUserId()));
    Branch branch = branchService.findById(request.getBranchId());
    Trainer trainer = new Trainer();
    trainer.setUser(user);
    trainer.setBranch(branch);
    applyRequest(trainer, request);
    return trainerRepository.save(trainer);
  }

  @Transactional
  public Trainer update(Long id, TrainerRequest request) {
    Trainer trainer = findById(id);
    User user =
        userRepository
            .findById(request.getUserId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "User not found with id: " + request.getUserId()));
    Branch branch = branchService.findById(request.getBranchId());
    trainer.setUser(user);
    trainer.setBranch(branch);
    applyRequest(trainer, request);
    return trainerRepository.save(trainer);
  }

  @Transactional
  public void delete(Long id) {
    Trainer trainer = findById(id);
    cascadeDeleteService.deleteTrainerCascade(trainer);
  }

  private void applyRequest(Trainer trainer, TrainerRequest request) {
    trainer.setSpecialization(request.getSpecialization());
    trainer.setBio(request.getBio());
    String status =
        request.getStatus() == null || request.getStatus().isBlank()
            ? "ACTIVE"
            : request.getStatus().toUpperCase();
    if (!VALID_STATUSES.contains(status)) {
      throw new BadRequestException("Invalid trainer status: " + status);
    }
    trainer.setStatus(status);
  }
}
