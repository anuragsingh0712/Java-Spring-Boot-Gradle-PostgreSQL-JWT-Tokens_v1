package com.example.app.repository;

import com.example.app.entity.Workout;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutRepository extends JpaRepository<Workout, Long> {

  List<Workout> findByMemberId(Long memberId);

  List<Workout> findByTrainerId(Long trainerId);
}
