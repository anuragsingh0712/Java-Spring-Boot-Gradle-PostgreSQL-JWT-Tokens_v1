package com.example.app.service;

import com.example.app.dto.MembershipRequest;
import com.example.app.entity.Member;
import com.example.app.entity.Membership;
import com.example.app.entity.Payment;
import com.example.app.exception.BadRequestException;
import com.example.app.exception.ResourceNotFoundException;
import com.example.app.repository.MembershipRepository;
import com.example.app.repository.PaymentRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MembershipService {

  private final MembershipRepository membershipRepository;

  private final MemberService memberService;

  private final NotificationService notificationService;

  private final PaymentRepository paymentRepository;

  public Page<Membership> findAll(Pageable pageable) {
    return membershipRepository.findAll(pageable);
  }

  public Membership findById(Long id) {
    return membershipRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Membership not found with id: " + id));
  }

  @Transactional
  public Membership purchase(MembershipRequest request) {
    Member member = memberService.findById(request.getMemberId());
    Membership membership = new Membership();
    membership.setMember(member);
    membership.setPlanName(request.getPlanName());
    membership.setPrice(request.getPrice());
    membership.setDurationDays(request.getDurationDays());
    membership.setStatus("PENDING");
    return membershipRepository.save(membership);
  }

  @Transactional
  public Membership activate(Long id) {
    Membership membership = findById(id);
    if (!"PENDING".equals(membership.getStatus())) {
      throw new BadRequestException("Only PENDING memberships can be activated");
    }
    membership.setStatus("ACTIVE");
    membership.setStartDate(LocalDate.now());
    membership.setEndDate(LocalDate.now().plusDays(membership.getDurationDays()));
    Membership saved = membershipRepository.save(membership);
    if (membership.getMember().getUser() != null) {
      notificationService.create(
          membership.getMember().getUser(),
          "MEMBERSHIP_ACTIVATED",
          "Your membership plan '" + membership.getPlanName() + "' has been activated.");
    }
    return saved;
  }

  @Transactional
  public Membership renew(Long id) {
    Membership membership = findById(id);
    if (!"ACTIVE".equals(membership.getStatus()) && !"EXPIRED".equals(membership.getStatus())) {
      throw new BadRequestException("Only ACTIVE or EXPIRED memberships can be renewed");
    }
    LocalDate base =
        membership.getEndDate() != null && membership.getEndDate().isAfter(LocalDate.now())
            ? membership.getEndDate()
            : LocalDate.now();
    membership.setStatus("ACTIVE");
    membership.setStartDate(
        membership.getStartDate() == null ? LocalDate.now() : membership.getStartDate());
    membership.setEndDate(base.plusDays(membership.getDurationDays()));
    return membershipRepository.save(membership);
  }

  @Transactional
  public Membership cancel(Long id) {
    Membership membership = findById(id);
    if ("CANCELLED".equals(membership.getStatus())) {
      throw new BadRequestException("Membership is already cancelled");
    }
    membership.setStatus("CANCELLED");
    return membershipRepository.save(membership);
  }

  @Transactional
  public void delete(Long id) {
    Membership membership = findById(id);
    for (Payment payment : paymentRepository.findByMembershipId(id)) {
      payment.setMembership(null);
      paymentRepository.save(payment);
    }
    membershipRepository.delete(membership);
  }
}
