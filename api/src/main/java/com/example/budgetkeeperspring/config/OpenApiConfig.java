package com.example.budgetkeeperspring.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI budgetKeeperOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Budget Keeper API")
                        .description("REST API for the Budget Keeper application")
                        .version("1.0.0"));
    }
}
