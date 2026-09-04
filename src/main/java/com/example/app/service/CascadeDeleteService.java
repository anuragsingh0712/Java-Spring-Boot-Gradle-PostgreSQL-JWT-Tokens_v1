package com.example.app.service;

import com.example.app.entity.Branch;
import com.example.app.entity.FitnessClass;
import com.example.app.entity.Gym;
import com.example.app.entity.Member;
import com.example.app.entity.Trainer;
import com.example.app.repository.AppointmentRepository;
import com.example.app.repository.AttendanceRepository;
import com.example.app.repository.BranchRepository;
import com.example.app.repository.ClassRegistrationRepository;
import com.example.app.repository.FitnessClassRepository;
import com.example.app.repository.GymRepository;
import com.example.app.repository.MemberRepository;
import com.example.app.repository.MembershipRepository;
import com.example.app.repository.PaymentRepository;
import com.example.app.repository.TrainerRepository;
import com.example.app.repository.WorkoutRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Centralizes deep-delete logic for entities that carry historical child records (registrations,
 * workouts, appointments, attendance, payments, memberships). Kept as a single repository-only
 * service (no dependency on the other *Service classes) to avoid circular bean dependencies between
 * Member/Trainer/Branch/Gym services, which already depend on each other for lookups.
 */
@Service
@RequiredArgsConstructor
public class CascadeDeleteService {

  private final MemberRepository memberRepository;

  private final TrainerRepository trainerRepository;

  private final BranchRepository branchRepository;

  private final GymRepository gymRepository;

  private final MembershipRepository membershipRepository;

  private final WorkoutRepository workoutRepository;

  private final FitnessClassRepository fitnessClassRepository;

  private final ClassRegistrationRepository classRegistrationRepository;

  private final AppointmentRepository appointmentRepository;

  private final AttendanceRepository attendanceRepository;

  private final PaymentRepository paymentRepository;

  @Transactional
  public void deleteMemberCascade(Member member) {
    classRegistrationRepository.deleteAll(
        classRegistrationRepository.findByMemberId(member.getId()));
    workoutRepository.deleteAll(workoutRepository.findByMemberId(member.getId()));
    appointmentRepository.deleteAll(appointmentRepository.findByMemberId(member.getId()));
    attendanceRepository.deleteAll(attendanceRepository.findByMemberId(member.getId()));
    paymentRepository.deleteAll(paymentRepository.findByMemberId(member.getId()));
    membershipRepository.deleteAll(membershipRepository.findByMemberId(member.getId()));
    memberRepository.delete(member);
  }

  @Transactional
  public void deleteFitnessClassCascade(FitnessClass fitnessClass) {
    classRegistrationRepository.deleteAll(
        classRegistrationRepository.findByFitnessClassId(fitnessClass.getId()));
    fitnessClassRepository.delete(fitnessClass);
  }

  @Transactional
  public void deleteTrainerCascade(Trainer trainer) {
    for (FitnessClass fitnessClass : fitnessClassRepository.findByTrainerId(trainer.getId())) {
      deleteFitnessClassCascade(fitnessClass);
    }
    workoutRepository.deleteAll(workoutRepository.findByTrainerId(trainer.getId()));
    appointmentRepository.deleteAll(appointmentRepository.findByTrainerId(trainer.getId()));
    trainerRepository.delete(trainer);
  }

  @Transactional
  public void deleteBranchCascade(Branch branch) {
    for (Member member : memberRepository.findByBranchId(branch.getId())) {
      deleteMemberCascade(member);
    }
    for (Trainer trainer : trainerRepository.findByBranchId(branch.getId())) {
      deleteTrainerCascade(trainer);
    }
    for (FitnessClass fitnessClass : fitnessClassRepository.findByBranchId(branch.getId())) {
      deleteFitnessClassCascade(fitnessClass);
    }
    branchRepository.delete(branch);
  }

  @Transactional
  public void deleteGymCascade(Gym gym) {
    for (Branch branch : branchRepository.findByGymId(gym.getId())) {
      deleteBranchCascade(branch);
    }
    gymRepository.delete(gym);
  }
}
