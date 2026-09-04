package com.example.app.service;

import com.example.app.dto.FitnessClassRequest;
import com.example.app.entity.Branch;
import com.example.app.entity.ClassRegistration;
import com.example.app.entity.FitnessClass;
import com.example.app.entity.Member;
import com.example.app.entity.Trainer;
import com.example.app.exception.BadRequestException;
import com.example.app.exception.ResourceNotFoundException;
import com.example.app.repository.ClassRegistrationRepository;
import com.example.app.repository.FitnessClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FitnessClassService {

  private final FitnessClassRepository fitnessClassRepository;

  private final ClassRegistrationRepository classRegistrationRepository;

  private final BranchService branchService;

  private final TrainerService trainerService;

  private final MemberService memberService;

  private final CascadeDeleteService cascadeDeleteService;

  public Page<FitnessClass> findAll(Pageable pageable) {
    return fitnessClassRepository.findAll(pageable);
  }

  public FitnessClass findById(Long id) {
    return fitnessClassRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Fitness class not found with id: " + id));
  }

  @Transactional
  public FitnessClass create(FitnessClassRequest request) {
    Branch branch = branchService.findById(request.getBranchId());
    Trainer trainer = trainerService.findById(request.getTrainerId());
    FitnessClass fitnessClass = new FitnessClass();
    fitnessClass.setBranch(branch);
    fitnessClass.setTrainer(trainer);
    applyRequest(fitnessClass, request);
    return fitnessClassRepository.save(fitnessClass);
  }

  @Transactional
  public FitnessClass update(Long id, FitnessClassRequest request) {
    FitnessClass fitnessClass = findById(id);
    Branch branch = branchService.findById(request.getBranchId());
    Trainer trainer = trainerService.findById(request.getTrainerId());
    fitnessClass.setBranch(branch);
    fitnessClass.setTrainer(trainer);
    applyRequest(fitnessClass, request);
    return fitnessClassRepository.save(fitnessClass);
  }

  @Transactional
  public void delete(Long id) {
    FitnessClass fitnessClass = findById(id);
    cascadeDeleteService.deleteFitnessClassCascade(fitnessClass);
  }

  @Transactional
  public ClassRegistration register(Long classId, Long memberId) {
    FitnessClass fitnessClass = findById(classId);
    Member member = memberService.findById(memberId);
    if (fitnessClass.getRegisteredCount() >= fitnessClass.getCapacity()) {
      throw new BadRequestException("Fitness class is already at full capacity");
    }
    classRegistrationRepository
        .findByFitnessClassIdAndMemberIdAndStatus(classId, memberId, "REGISTERED")
        .ifPresent(
            r -> {
              throw new BadRequestException("Member is already registered for this class");
            });
    ClassRegistration registration = new ClassRegistration();
    registration.setFitnessClass(fitnessClass);
    registration.setMember(member);
    registration.setStatus("REGISTERED");
    ClassRegistration saved = classRegistrationRepository.save(registration);
    fitnessClass.setRegisteredCount(fitnessClass.getRegisteredCount() + 1);
    fitnessClassRepository.save(fitnessClass);
    return saved;
  }

  @Transactional
  public void cancelRegistration(Long classId, Long memberId) {
    ClassRegistration registration =
        classRegistrationRepository
            .findByFitnessClassIdAndMemberIdAndStatus(classId, memberId, "REGISTERED")
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Active registration not found for this member and class"));
    registration.setStatus("CANCELLED");
    classRegistrationRepository.save(registration);
    FitnessClass fitnessClass = registration.getFitnessClass();
    fitnessClass.setRegisteredCount(Math.max(0, fitnessClass.getRegisteredCount() - 1));
    fitnessClassRepository.save(fitnessClass);
  }

  private void applyRequest(FitnessClass fitnessClass, FitnessClassRequest request) {
    fitnessClass.setName(request.getName());
    fitnessClass.setType(request.getType());
    fitnessClass.setScheduleTime(request.getScheduleTime());
    fitnessClass.setCapacity(request.getCapacity());
  }
}
