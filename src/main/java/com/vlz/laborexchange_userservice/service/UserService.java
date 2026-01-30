package com.vlz.laborexchange_userservice.service;

import com.vlz.laborexchange_userservice.dto.RegisterRequest;
import com.vlz.laborexchange_userservice.dto.exception.EntityNotFoundException;
import com.vlz.laborexchange_userservice.entity.Role;
import com.vlz.laborexchange_userservice.entity.User;
import com.vlz.laborexchange_userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleService roleService;

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
                .role(roleService.findByRoleName(registerRequest.getUserRole()))
                .build();

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public String getEmailById(Long id) {
        return userRepository.getEmailById(id);
    }

    @Transactional(readOnly = true)
    public Long getUserIdByEmail(String email) {
        return userRepository.getUserIdByEmail(email);
    }
}
