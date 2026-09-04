package com.example.app.service;

import com.example.app.dto.AppointmentRequest;
import com.example.app.entity.Appointment;
import com.example.app.entity.Member;
import com.example.app.entity.Trainer;
import com.example.app.exception.BadRequestException;
import com.example.app.exception.ResourceNotFoundException;
import com.example.app.repository.AppointmentRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AppointmentService {

  private static final Set<String> VALID_STATUSES = Set.of("SCHEDULED", "COMPLETED", "CANCELLED");

  private final AppointmentRepository appointmentRepository;

  private final MemberService memberService;

  private final TrainerService trainerService;

  public Page<Appointment> findAll(Pageable pageable) {
    return appointmentRepository.findAll(pageable);
  }

  public Appointment findById(Long id) {
    return appointmentRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));
  }

  @Transactional
  public Appointment create(AppointmentRequest request) {
    Member member = memberService.findById(request.getMemberId());
    Trainer trainer = trainerService.findById(request.getTrainerId());
    int duration = request.getDurationMinutes() <= 0 ? 60 : request.getDurationMinutes();
    checkConflict(trainer.getId(), request.getScheduledAt(), duration, null);
    Appointment appointment = new Appointment();
    appointment.setMember(member);
    appointment.setTrainer(trainer);
    appointment.setScheduledAt(request.getScheduledAt());
    appointment.setDurationMinutes(duration);
    appointment.setStatus("SCHEDULED");
    return appointmentRepository.save(appointment);
  }

  @Transactional
  public Appointment update(Long id, AppointmentRequest request) {
    Appointment appointment = findById(id);
    Member member = memberService.findById(request.getMemberId());
    Trainer trainer = trainerService.findById(request.getTrainerId());
    int duration = request.getDurationMinutes() <= 0 ? 60 : request.getDurationMinutes();
    checkConflict(trainer.getId(), request.getScheduledAt(), duration, id);
    appointment.setMember(member);
    appointment.setTrainer(trainer);
    appointment.setScheduledAt(request.getScheduledAt());
    appointment.setDurationMinutes(duration);
    return appointmentRepository.save(appointment);
  }

  @Transactional
  public Appointment updateStatus(Long id, String status) {
    Appointment appointment = findById(id);
    String upper = status.toUpperCase();
    if (!VALID_STATUSES.contains(upper)) {
      throw new BadRequestException("Invalid appointment status: " + upper);
    }
    appointment.setStatus(upper);
    return appointmentRepository.save(appointment);
  }

  @Transactional
  public void delete(Long id) {
    Appointment appointment = findById(id);
    appointmentRepository.delete(appointment);
  }

  private void checkConflict(
      Long trainerId, LocalDateTime scheduledAt, int durationMinutes, Long excludeId) {
    LocalDateTime newStart = scheduledAt;
    LocalDateTime newEnd = scheduledAt.plusMinutes(durationMinutes);
    List<Appointment> existing =
        appointmentRepository.findByTrainerIdAndStatusNot(trainerId, "CANCELLED");
    for (Appointment appointment : existing) {
      if (excludeId != null && appointment.getId().equals(excludeId)) {
        continue;
      }
      LocalDateTime existingStart = appointment.getScheduledAt();
      LocalDateTime existingEnd = existingStart.plusMinutes(appointment.getDurationMinutes());
      if (newStart.isBefore(existingEnd) && existingStart.isBefore(newEnd)) {
        throw new BadRequestException(
            "Trainer already has an appointment overlapping this time slot");
      }
    }
  }
}
