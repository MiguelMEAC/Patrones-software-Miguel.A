package com.smartgrid.smartgrid.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SmartGridConfiguration {

    @Bean
    public SmartGridConfig smartGridConfig() {
        return SmartGridConfig.getInstancia();
    }
}