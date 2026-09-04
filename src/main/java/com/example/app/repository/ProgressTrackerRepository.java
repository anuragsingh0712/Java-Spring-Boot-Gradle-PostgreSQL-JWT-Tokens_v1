package com.example.app.repository;

import com.example.app.entity.ProgressTracker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProgressTrackerRepository extends JpaRepository<ProgressTracker, Long> {

  Page<ProgressTracker> findByMemberId(Long memberId, Pageable pageable);
}
