package com.example.budgetkeeperspring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;


@Configuration
public class SecurityConfiguration {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        /* CUSTOM SECURITY */
        httpSecurity
                .csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers(HttpMethod.OPTIONS, "/**")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic();

        return httpSecurity.build();
    }

    @Bean
    public JdbcUserDetailsManager userDetailsManager(DataSource dataSource) {
        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
        jdbcUserDetailsManager.setUsersByUsernameQuery("select username, password, enabled from users where username=?");
        jdbcUserDetailsManager.setAuthoritiesByUsernameQuery("select username, role from users where username=?");
        return jdbcUserDetailsManager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

