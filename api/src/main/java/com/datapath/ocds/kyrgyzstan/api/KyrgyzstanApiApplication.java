package com.datapath.ocds.kyrgyzstan.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class KyrgyzstanApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(KyrgyzstanApiApplication.class, args);
    }

    @Bean
    public EventMerger eventMerger(ObjectMapper objectMapper) {
        return new EventMerger(objectMapper);
    }

}