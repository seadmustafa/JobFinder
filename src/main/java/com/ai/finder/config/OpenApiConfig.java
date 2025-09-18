package com.ai.jobfinder.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI jobNotifierApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Job Finder API")
                        .description("API for searching and notifying about jobs")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("API Support")
                                .email("support@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")));
    }
}