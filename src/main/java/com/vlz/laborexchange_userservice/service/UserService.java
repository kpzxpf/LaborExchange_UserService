package com.vlz.laborexchange_userservice.service;

import com.vlz.laborexchange_userservice.dto.RegisterRequest;
import com.vlz.laborexchange_userservice.dto.exception.EntityNotFoundException;
import com.vlz.laborexchange_userservice.entity.Role;
import com.vlz.laborexchange_userservice.entity.User;
import com.vlz.laborexchange_userservice.repository.RoleRepository;
import com.vlz.laborexchange_userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public boolean existsUserByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public boolean checkLogin(String email, String password) {
        return userRepository.existsByEmailAndPassword(email, password);
    }

    @Transactional
    public void create(RegisterRequest registerRequest) {
        User user = User.builder()
                .email(registerRequest.getEmail())
                .username(registerRequest.getUsername())
                .phoneNumber(registerRequest.getPhone())
                .password(registerRequest.getPassword())
                .roles(new HashSet<>())
                .build();

        String roleNameFromRequest = registerRequest.getUserRole();

        Role role = roleRepository.findByRoleName(roleNameFromRequest)
                .orElseThrow(() -> {
                    log.error("Role not found with name {}", roleNameFromRequest);
                    return new EntityNotFoundException("Role not found with name " + roleNameFromRequest);
                });
        user.getRoles().add(role);

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public String getEmailById(Long id) {
        return userRepository.getEmailById(id);
    }
}
