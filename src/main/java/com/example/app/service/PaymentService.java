package com.example.app.service;

import com.example.app.dto.PaymentRequest;
import com.example.app.entity.Member;
import com.example.app.entity.Membership;
import com.example.app.entity.Payment;
import com.example.app.exception.BadRequestException;
import com.example.app.exception.ResourceNotFoundException;
import com.example.app.repository.PaymentRepository;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

  private static final Set<String> VALID_STATUSES = Set.of("PENDING", "SUCCESS", "FAILED");

  private final PaymentRepository paymentRepository;

  private final MemberService memberService;

  private final MembershipService membershipService;

  private final NotificationService notificationService;

  public Page<Payment> findAll(Pageable pageable) {
    return paymentRepository.findAll(pageable);
  }

  public Payment findById(Long id) {
    return paymentRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
  }

  @Transactional
  public Payment create(PaymentRequest request) {
    Member member = memberService.findById(request.getMemberId());
    Payment payment = new Payment();
    payment.setMember(member);
    if (request.getMembershipId() != null) {
      Membership membership = membershipService.findById(request.getMembershipId());
      payment.setMembership(membership);
    }
    payment.setAmount(request.getAmount());
    payment.setMethod(request.getMethod());
    payment.setStatus("PENDING");
    payment.setTransactionDate(LocalDateTime.now());
    return paymentRepository.save(payment);
  }

  @Transactional
  public Payment updateStatus(Long id, String status) {
    Payment payment = findById(id);
    String upper = status.toUpperCase();
    if (!VALID_STATUSES.contains(upper)) {
      throw new BadRequestException("Invalid payment status: " + upper);
    }
    payment.setStatus(upper);
    payment.setTransactionDate(LocalDateTime.now());
    Payment saved = paymentRepository.save(payment);
    if (payment.getMember().getUser() != null) {
      String message =
          "SUCCESS".equals(upper)
              ? "Your payment of " + payment.getAmount() + " was successful."
              : "FAILED".equals(upper)
                  ? "Your payment of " + payment.getAmount() + " has failed."
                  : "Your payment of " + payment.getAmount() + " is pending.";
      notificationService.create(payment.getMember().getUser(), "PAYMENT_" + upper, message);
    }
    return saved;
  }

  @Transactional
  public void delete(Long id) {
    Payment payment = findById(id);
    paymentRepository.delete(payment);
  }
}
