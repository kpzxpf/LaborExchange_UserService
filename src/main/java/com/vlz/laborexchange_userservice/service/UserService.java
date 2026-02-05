package com.vlz.laborexchange_userservice.service;

import com.vlz.laborexchange_userservice.dto.RegisterRequest;
import com.vlz.laborexchange_userservice.dto.UserDto;
import com.vlz.laborexchange_userservice.exception.EntityNotFoundException;
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

    @Transactional
    public User update(UserDto userDto) {
        User user = getById(userDto.getId());

        user.setUsername(userDto.getUsername());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setEmail(userDto.getEmail());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setUsername(userDto.getUsername());

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public String getEmailById(Long id) {
        return userRepository.getEmailById(id);
    }

    @Transactional(readOnly = true)
    public Long getUserIdByEmail(String email) {
        return userRepository.getUserIdByEmail(email);
    }

    @Transactional(readOnly = true)
    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> {
            log.error("User not found with id {}", id);
            return new EntityNotFoundException("User not found");
        });
    }

    @Transactional(readOnly = true)
    public UserDto getUserProfile(Long id) {
        User user = getById(id);

        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .roleName(user.getRole().getRoleName())
                .build();
    }
}
