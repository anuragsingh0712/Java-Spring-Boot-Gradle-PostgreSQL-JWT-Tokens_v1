package com.example.app.repository;

import com.example.app.entity.Trainer;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainerRepository extends JpaRepository<Trainer, Long> {

  Optional<Trainer> findByUserId(Long userId);

  List<Trainer> findByBranchId(Long branchId);
}
