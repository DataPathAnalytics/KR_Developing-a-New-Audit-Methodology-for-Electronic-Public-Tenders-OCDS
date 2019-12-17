package com.datapath.ocds.kyrgyzstan.exporter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableScheduling
public class KyrgyzstanExporterApplication {

    public static void main(String[] args) {
        SpringApplication.run(KyrgyzstanExporterApplication.class, args);
    }

    @Bean
    public RestTemplate getRestTemplate(@Value("${api.url}") String apiUrl) {
        return new RestTemplateBuilder().rootUri(apiUrl).build();
    }

}