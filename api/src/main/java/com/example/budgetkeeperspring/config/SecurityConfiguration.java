package com.example.budgetkeeperspring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

import javax.sql.DataSource;


@Configuration
public class SecurityConfiguration {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        // Tomcat rejestruje też JspServlet, więc String-owe matchery nie wiedzą, czy to ścieżki MVC (CVE-2023-34035).
        PathPatternRequestMatcher.Builder path = PathPatternRequestMatcher.withDefaults();

        /* CUSTOM SECURITY */
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(path.matcher(HttpMethod.OPTIONS, "/**"))
                        .permitAll()
                        .requestMatchers(path.matcher("/swagger-ui/**"), path.matcher("/v3/api-docs/**"), path.matcher("/swagger-ui.html"))
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                // Basic auth domyślnie nie zapisuje kontekstu w sesji, więc każdy request odpytywał bazę i liczył bcrypt.
                .httpBasic(basic -> basic.securityContextRepository(new HttpSessionSecurityContextRepository()));

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
