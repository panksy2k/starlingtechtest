package com.starlingbank.tech.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BeanConfig {

    @Autowired
    private StarlingPropertyConfigurationHolder starlingPropertyConfigurationHolder;

  /*  @Bean
    public WebClient webClient() {
        return WebClient.create(starlingPropertyConfigurationHolder.getApiUrl());
    }
    */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
