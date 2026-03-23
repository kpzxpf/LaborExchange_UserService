package com.vlz.laborexchange_userservice.service;

import com.vlz.laborexchange_userservice.dto.AdminUserDto;
import com.vlz.laborexchange_userservice.entity.User;
import com.vlz.laborexchange_userservice.exception.EntityNotFoundException;
import com.vlz.laborexchange_userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<AdminUserDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::toDto);
    }

    @Transactional
    public void deactivate(Long userId) {
        User user = getUser(userId);

        user.setActive(false);
        userRepository.save(user);

        log.info("Admin deactivated user {}", userId);
    }

    @Transactional
    public void activate(Long userId) {
        User user = getUser(userId);

        user.setActive(true);
        userRepository.save(user);

        log.info("Admin activated user {}", userId);
    }

    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found");
        }

        userRepository.deleteById(userId);

        log.info("Admin deleted user {}", userId);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    private AdminUserDto toDto(User user) {
        return AdminUserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .roleName(user.getRole() != null ? user.getRole().getRoleName() : null)
                .emailVerified(user.isEmailVerified())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
