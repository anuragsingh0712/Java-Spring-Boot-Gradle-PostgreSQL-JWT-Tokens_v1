package com.example.app.repository;

import com.example.app.entity.FitnessClass;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FitnessClassRepository extends JpaRepository<FitnessClass, Long> {

  List<FitnessClass> findByTrainerId(Long trainerId);

  List<FitnessClass> findByBranchId(Long branchId);
}
