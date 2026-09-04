package com.example.app.repository;

import com.example.app.entity.Branch;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BranchRepository extends JpaRepository<Branch, Long> {

  List<Branch> findByGymId(Long gymId);
}
