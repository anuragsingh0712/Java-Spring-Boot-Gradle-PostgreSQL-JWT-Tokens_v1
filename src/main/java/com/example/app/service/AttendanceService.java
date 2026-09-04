package com.example.app.service;

import com.example.app.entity.Attendance;
import com.example.app.entity.Member;
import com.example.app.exception.BadRequestException;
import com.example.app.exception.ResourceNotFoundException;
import com.example.app.repository.AttendanceRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AttendanceService {

  private final AttendanceRepository attendanceRepository;

  private final MemberService memberService;

  public Page<Attendance> findAll(Pageable pageable) {
    return attendanceRepository.findAll(pageable);
  }

  public Attendance findById(Long id) {
    return attendanceRepository
        .findById(id)
        .orElseThrow(
            () -> new ResourceNotFoundException("Attendance record not found with id: " + id));
  }

  @Transactional
  public Attendance checkIn(Long memberId) {
    Member member = memberService.findById(memberId);
    Attendance attendance = new Attendance();
    attendance.setMember(member);
    attendance.setCheckInTime(LocalDateTime.now());
    return attendanceRepository.save(attendance);
  }

  @Transactional
  public Attendance checkOut(Long id) {
    Attendance attendance = findById(id);
    if (attendance.getCheckOutTime() != null) {
      throw new BadRequestException("Member has already checked out of this session");
    }
    attendance.setCheckOutTime(LocalDateTime.now());
    return attendanceRepository.save(attendance);
  }

  @Transactional
  public void delete(Long id) {
    Attendance attendance = findById(id);
    attendanceRepository.delete(attendance);
  }
}
