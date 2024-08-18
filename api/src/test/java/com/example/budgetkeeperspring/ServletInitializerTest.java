package com.example.budgetkeeperspring;

import org.junit.jupiter.api.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ServletInitializerTest extends SpringBootServletInitializer {

    @Test
    void configure_returnsNonNullBuilder() {
        ServletInitializer servletInitializer = new ServletInitializer();
        SpringApplicationBuilder builder = servletInitializer.configure(new SpringApplicationBuilder());
        assertNotNull(builder);
    }
}