package com.house.keeping.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class NacosConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}