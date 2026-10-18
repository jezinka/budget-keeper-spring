package com.example.budgetkeeperspring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import javax.sql.DataSource;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;


@Configuration
public class SecurityConfiguration {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        /* CUSTOM SECURITY */
        httpSecurity
                .csrf().disable()
                .authorizeHttpRequests()
                // Tomcat rejestruje też JspServlet, więc String-owe matchery nie wiedzą, czy to ścieżki MVC (CVE-2023-34035).
                .requestMatchers(antMatcher(HttpMethod.OPTIONS, "/**"))
                .permitAll()
                .requestMatchers(antMatcher("/swagger-ui/**"), antMatcher("/v3/api-docs/**"), antMatcher("/swagger-ui.html"))
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic()
                // Basic auth domyślnie nie zapisuje kontekstu w sesji, więc każdy request odpytywał bazę i liczył bcrypt.
                .withObjectPostProcessor(new ObjectPostProcessor<BasicAuthenticationFilter>() {
                    @Override
                    public <O extends BasicAuthenticationFilter> O postProcess(O filter) {
                        filter.setSecurityContextRepository(new HttpSessionSecurityContextRepository());
                        return filter;
                    }
                });

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

