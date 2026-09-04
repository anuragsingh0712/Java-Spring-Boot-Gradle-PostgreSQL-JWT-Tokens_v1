package com.example.app.repository;

import com.example.app.entity.Appointment;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

  List<Appointment> findByTrainerIdAndStatusNot(Long trainerId, String status);

  List<Appointment> findByTrainerIdAndScheduledAtBetween(
      Long trainerId, LocalDateTime start, LocalDateTime end);

  List<Appointment> findByMemberId(Long memberId);

  List<Appointment> findByTrainerId(Long trainerId);
}
