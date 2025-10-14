package com.vlz.laborexchange_userservice.listener;

import com.vlz.laborexchange_userservice.dto.RegisterRequest;
import com.vlz.laborexchange_userservice.entity.User;
import com.vlz.laborexchange_userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserRegistrationListener {
    private final UserRepository userRepository;


    @KafkaListener(topics = "${topics.user-registration}", groupId = "user-group")
    public void handleUserRegistration(RegisterRequest request) {
        try {
            User user = new User();
            user.setEmail(request.getEmail());
            user.setPassword(request.getPassword());
            userRepository.save(user);

            log.info("User registered in DB: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Error handling user registration for email: {}", request.getEmail(), e);
            throw e;
        }
    }
}