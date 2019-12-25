package com.starlingbank.tech.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "starling.client")
@Data
public class StarlingPropertyConfigurationHolder {
    private String clientAuthToken;
    private String apiBaseUrl;
}
