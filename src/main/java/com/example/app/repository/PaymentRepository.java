package com.example.app.repository;

import com.example.app.entity.Payment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

  List<Payment> findByMemberId(Long memberId);

  List<Payment> findByMembershipId(Long membershipId);
}
