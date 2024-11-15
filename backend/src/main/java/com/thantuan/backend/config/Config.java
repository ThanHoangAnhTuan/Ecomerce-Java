package com.thantuan.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

@Configuration
public class Config {
    @Bean
    public AuditorAware<String> auditorAware() {
        return new ApplicationAuditAware();
    }
}
