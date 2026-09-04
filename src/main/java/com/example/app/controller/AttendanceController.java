package com.example.app.controller;

import com.example.app.dto.AttendanceCheckInRequest;
import com.example.app.entity.Attendance;
import com.example.app.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/attendance")
@RequiredArgsConstructor
@Tag(name = "Attendance", description = "Member check-in/check-out tracking")
public class AttendanceController {

  private final AttendanceService attendanceService;

  @GetMapping
  @Operation(summary = "List all attendance records (paginated)")
  public ResponseEntity<Page<Attendance>> findAll(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
    return ResponseEntity.ok(attendanceService.findAll(PageRequest.of(page, size)));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get an attendance record by id")
  public ResponseEntity<Attendance> findById(@PathVariable Long id) {
    return ResponseEntity.ok(attendanceService.findById(id));
  }

  @PostMapping("/checkin")
  @Operation(summary = "Check in a member")
  public ResponseEntity<Attendance> checkIn(@Valid @RequestBody AttendanceCheckInRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(attendanceService.checkIn(request.getMemberId()));
  }

  @PutMapping("/{id}/checkout")
  @Operation(summary = "Check out a member from an attendance record")
  public ResponseEntity<Attendance> checkOut(@PathVariable Long id) {
    return ResponseEntity.ok(attendanceService.checkOut(id));
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Delete an attendance record")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    attendanceService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
