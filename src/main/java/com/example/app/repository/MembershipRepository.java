package com.example.app.repository;

import com.example.app.entity.Membership;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MembershipRepository extends JpaRepository<Membership, Long> {

  List<Membership> findByMemberId(Long memberId);
}
