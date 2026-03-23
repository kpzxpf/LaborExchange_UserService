package com.vlz.laborexchange_userservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vlz.laborexchange_userservice.event.PasswordResetEmailEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PasswordResetProducer extends AbstractProducer<PasswordResetEmailEvent> {

    @Value("${spring.kafka.topics.password-reset}")
    private String topic;

    public PasswordResetProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        super(kafkaTemplate, objectMapper);
    }

    public void send(PasswordResetEmailEvent event) {
        sendMessage(topic, event);
    }
}
