package com.dam.config.mail;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 读取yml配置文件里面的信息
 */
@Configuration
@ConfigurationProperties(prefix = "spring.mail")
@Data
public class MailConfig {
//    private String email;
    private String host;
    private String port;
    private String username;
    private String password;
}
