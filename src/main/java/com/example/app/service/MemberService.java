package com.example.app.service;

import com.example.app.dto.MemberRequest;
import com.example.app.entity.Branch;
import com.example.app.entity.Member;
import com.example.app.entity.User;
import com.example.app.exception.BadRequestException;
import com.example.app.exception.ResourceNotFoundException;
import com.example.app.repository.MemberRepository;
import com.example.app.repository.UserRepository;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

  private static final Set<String> VALID_STATUSES = Set.of("ACTIVE", "INACTIVE", "SUSPENDED");

  private final MemberRepository memberRepository;

  private final UserRepository userRepository;

  private final BranchService branchService;

  private final CascadeDeleteService cascadeDeleteService;

  public Page<Member> findAll(Pageable pageable) {
    return memberRepository.findAll(pageable);
  }

  public Member findById(Long id) {
    return memberRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + id));
  }

  public Member findByUserId(Long userId) {
    return memberRepository
        .findByUserId(userId)
        .orElseThrow(
            () -> new ResourceNotFoundException("Member profile not found for current user"));
  }

  @Transactional
  public Member create(MemberRequest request) {
    Branch branch = branchService.findById(request.getBranchId());
    Member member = new Member();
    member.setBranch(branch);
    if (request.getUserId() != null) {
      User user =
          userRepository
              .findById(request.getUserId())
              .orElseThrow(
                  () ->
                      new ResourceNotFoundException(
                          "User not found with id: " + request.getUserId()));
      member.setUser(user);
    }
    applyRequest(member, request);
    return memberRepository.save(member);
  }

  @Transactional
  public Member update(Long id, MemberRequest request) {
    Member member = findById(id);
    Branch branch = branchService.findById(request.getBranchId());
    member.setBranch(branch);
    if (request.getUserId() != null) {
      User user =
          userRepository
              .findById(request.getUserId())
              .orElseThrow(
                  () ->
                      new ResourceNotFoundException(
                          "User not found with id: " + request.getUserId()));
      member.setUser(user);
    }
    applyRequest(member, request);
    return memberRepository.save(member);
  }

  @Transactional
  public void delete(Long id) {
    Member member = findById(id);
    cascadeDeleteService.deleteMemberCascade(member);
  }

  private void applyRequest(Member member, MemberRequest request) {
    member.setFirstName(request.getFirstName());
    member.setLastName(request.getLastName());
    member.setEmail(request.getEmail());
    member.setPhone(request.getPhone());
    member.setDateOfBirth(request.getDateOfBirth());
    member.setJoinDate(request.getJoinDate());
    String status =
        request.getStatus() == null || request.getStatus().isBlank()
            ? "ACTIVE"
            : request.getStatus().toUpperCase();
    if (!VALID_STATUSES.contains(status)) {
      throw new BadRequestException("Invalid member status: " + status);
    }
    member.setStatus(status);
  }
}
