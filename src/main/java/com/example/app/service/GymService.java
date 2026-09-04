package com.example.app.service;

import com.example.app.dto.GymRequest;
import com.example.app.entity.Gym;
import com.example.app.exception.ResourceNotFoundException;
import com.example.app.repository.GymRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GymService {

  private final GymRepository gymRepository;

  private final CascadeDeleteService cascadeDeleteService;

  public Page<Gym> findAll(Pageable pageable) {
    return gymRepository.findAll(pageable);
  }

  public Gym findById(Long id) {
    return gymRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Gym not found with id: " + id));
  }

  @Transactional
  public Gym create(GymRequest request) {
    Gym gym = new Gym();
    gym.setName(request.getName());
    gym.setDescription(request.getDescription());
    gym.setAddress(request.getAddress());
    return gymRepository.save(gym);
  }

  @Transactional
  public Gym update(Long id, GymRequest request) {
    Gym gym = findById(id);
    gym.setName(request.getName());
    gym.setDescription(request.getDescription());
    gym.setAddress(request.getAddress());
    return gymRepository.save(gym);
  }

  @Transactional
  public void delete(Long id) {
    Gym gym = findById(id);
    cascadeDeleteService.deleteGymCascade(gym);
  }
}
