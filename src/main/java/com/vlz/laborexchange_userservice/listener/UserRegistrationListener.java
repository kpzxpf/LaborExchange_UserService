package com.vlz.laborexchange_userservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vlz.laborexchange_userservice.dto.RegisterRequest;
import com.vlz.laborexchange_userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserRegistrationListener {
    private final UserService userService;
    private final ObjectMapper mapper;

    @KafkaListener(topics = "${spring.kafka.topics.user-registration}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(String message) {
        try {
            RegisterRequest request = mapper.readValue(message, RegisterRequest.class);
            log.info("Event received: {}", request);

            userService.create(request);
        } catch (Exception e) {
            log.error("JSON parsing error", e);
        }
    }
}