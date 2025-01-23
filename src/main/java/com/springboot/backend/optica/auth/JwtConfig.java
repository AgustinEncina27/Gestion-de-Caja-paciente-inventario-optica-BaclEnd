package com.springboot.backend.optica.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    public String getSecretKey() {
        return secretKey;
    }
}
