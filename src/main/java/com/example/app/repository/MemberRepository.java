package com.example.app.repository;

import com.example.app.entity.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

  Optional<Member> findByUserId(Long userId);

  List<Member> findByBranchId(Long branchId);
}
