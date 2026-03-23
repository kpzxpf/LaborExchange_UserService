package com.vlz.laborexchange_userservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vlz.laborexchange_userservice.event.EmailVerificationEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EmailVerificationProducer extends AbstractProducer<EmailVerificationEvent> {

    @Value("${spring.kafka.topics.email-verification}")
    private String topic;

    public EmailVerificationProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        super(kafkaTemplate, objectMapper);
    }

    public void send(EmailVerificationEvent event) {
        sendMessage(topic, event);
    }
}
