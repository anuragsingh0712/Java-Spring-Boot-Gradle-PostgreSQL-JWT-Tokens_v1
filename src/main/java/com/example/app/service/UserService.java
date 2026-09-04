package com.example.app.service;

import com.example.app.dto.UserDto;
import com.example.app.dto.UserUpdateRequest;
import com.example.app.entity.User;
import com.example.app.exception.ResourceNotFoundException;
import com.example.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  private final PasswordEncoder passwordEncoder;

  public Page<UserDto> findAll(Pageable pageable) {
    return userRepository.findAll(pageable).map(this::toDto);
  }

  public UserDto findById(Long id) {
    return toDto(getEntity(id));
  }

  public User getEntity(Long id) {
    return userRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
  }

  @Transactional
  public UserDto update(Long id, UserUpdateRequest request) {
    User user = getEntity(id);
    if (request.getName() != null && !request.getName().isBlank()) {
      user.setName(request.getName());
    }
    if (request.getPassword() != null && !request.getPassword().isBlank()) {
      user.setPassword(passwordEncoder.encode(request.getPassword()));
    }
    if (request.getRole() != null && !request.getRole().isBlank()) {
      user.setRole(request.getRole().toUpperCase());
    }
    return toDto(userRepository.save(user));
  }

  @Transactional
  public void delete(Long id) {
    User user = getEntity(id);
    userRepository.delete(user);
  }

  private UserDto toDto(User user) {
    return new UserDto(
        user.getId(), user.getName(), user.getEmail(), user.getRole(), user.getCreatedAt());
  }
}
