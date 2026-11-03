package com.ibm.capstone.ebookstore.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI ebookstoreOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("E-Bookstore Platform API")
                        .description("AI Specialist Capstone Project - Backend REST APIs for E-Commerce Book Platform. Built with IBM BOB Agentic IDE.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("AI Specialist Team")
                                .email("capstone-support@ibm.com"))
                        .license(new License().name("Apache 2.0").url("https://springdoc.org")));
    }
}
