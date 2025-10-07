package com.innowise.securitystarter;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.innowise.securitystarter")
public class SecurityStarterAutoConfiguration {
    public static void main(String[] args) {
        SpringApplication.run(SecurityStarterAutoConfiguration.class, args);
    }
}