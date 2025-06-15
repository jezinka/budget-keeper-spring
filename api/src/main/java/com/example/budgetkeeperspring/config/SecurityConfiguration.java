package com.example.budgetkeeperspring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SecurityConfiguration {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf().disable()
                .authorizeHttpRequests(auth -> auth.requestMatchers("/expenses/**").authenticated()
                        .anyRequest().permitAll()
                )
                .oauth2Login();
        return http.build();
    }
}

