package com.dam.config.register;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "sss.register")
@Data
public class RegisterConfig {
    private String codeExpiration;
}
