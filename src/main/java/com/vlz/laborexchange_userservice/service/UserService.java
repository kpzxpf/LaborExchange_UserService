package com.vlz.laborexchange_userservice.service;

import com.vlz.laborexchange_userservice.dto.RegisterRequest;
import com.vlz.laborexchange_userservice.entity.User;
import com.vlz.laborexchange_userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

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
        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setUsername(registerRequest.getUsername());
        user.setPhoneNumber(registerRequest.getPhone());
        user.setPassword(registerRequest.getPassword());

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public String getEmailById(Long id) {
        return userRepository.getEmailByUserId(id);
    }
}
