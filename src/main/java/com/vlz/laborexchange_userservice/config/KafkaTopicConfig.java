package com.vlz.laborexchange_userservice.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;
    @Value("${spring.kafka.topics.user-registration}")
    private String userRegistrationTopic;
    @Value("${spring.kafka.topics.email-verification}")
    private String emailVerificationTopic;
    @Value("${spring.kafka.topics.password-reset}")
    private String passwordResetTopic;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic userRegisterTopic() {
        return new NewTopic(userRegistrationTopic, 1, (short) 1);
    }

    @Bean
    public NewTopic emailVerificationTopic() {
        return new NewTopic(emailVerificationTopic, 1, (short) 1);
    }

    @Bean
    public NewTopic passwordResetTopic() {
        return new NewTopic(passwordResetTopic, 1, (short) 1);
    }
}