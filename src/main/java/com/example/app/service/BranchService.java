package com.example.app.service;

import com.example.app.dto.BranchRequest;
import com.example.app.entity.Branch;
import com.example.app.entity.Gym;
import com.example.app.exception.ResourceNotFoundException;
import com.example.app.repository.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BranchService {

  private final BranchRepository branchRepository;

  private final GymService gymService;

  private final CascadeDeleteService cascadeDeleteService;

  public Page<Branch> findAll(Pageable pageable) {
    return branchRepository.findAll(pageable);
  }

  public Branch findById(Long id) {
    return branchRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Branch not found with id: " + id));
  }

  @Transactional
  public Branch create(BranchRequest request) {
    Gym gym = gymService.findById(request.getGymId());
    Branch branch = new Branch();
    branch.setGym(gym);
    applyRequest(branch, request);
    return branchRepository.save(branch);
  }

  @Transactional
  public Branch update(Long id, BranchRequest request) {
    Branch branch = findById(id);
    Gym gym = gymService.findById(request.getGymId());
    branch.setGym(gym);
    applyRequest(branch, request);
    return branchRepository.save(branch);
  }

  @Transactional
  public void delete(Long id) {
    Branch branch = findById(id);
    cascadeDeleteService.deleteBranchCascade(branch);
  }

  private void applyRequest(Branch branch, BranchRequest request) {
    branch.setName(request.getName());
    branch.setAddress(request.getAddress());
    branch.setOpeningTime(request.getOpeningTime());
    branch.setClosingTime(request.getClosingTime());
    branch.setFacilities(request.getFacilities());
    branch.setManagerId(request.getManagerId());
  }
}
