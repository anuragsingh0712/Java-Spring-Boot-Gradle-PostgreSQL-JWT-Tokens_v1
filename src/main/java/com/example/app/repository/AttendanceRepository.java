package com.example.app.repository;

import com.example.app.entity.Attendance;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

  List<Attendance> findByMemberId(Long memberId);
}
