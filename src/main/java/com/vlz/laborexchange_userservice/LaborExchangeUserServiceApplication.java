package com.vlz.laborexchange_userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@EnableCaching
@EnableFeignClients
@SpringBootApplication
public class LaborExchangeUserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(LaborExchangeUserServiceApplication.class, args);
    }
}
