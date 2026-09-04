package com.example.app.repository;

import com.example.app.entity.ClassRegistration;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassRegistrationRepository extends JpaRepository<ClassRegistration, Long> {

  List<ClassRegistration> findByFitnessClassId(Long classId);

  List<ClassRegistration> findByMemberId(Long memberId);

  Optional<ClassRegistration> findByFitnessClassIdAndMemberIdAndStatus(
      Long classId, Long memberId, String status);
}
