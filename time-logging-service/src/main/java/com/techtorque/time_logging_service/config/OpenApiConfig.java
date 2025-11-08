package com.techtorque.time_logging_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration for Time Logging Service
 * 
 * Configures API documentation with:
 * - Service information and contact details
 * - Security schemes (Bearer JWT)
 * - Server URLs for different environments
 * 
 * Access Swagger UI at: http://localhost:8085/swagger-ui/index.html
 * Access API docs JSON at: http://localhost:8085/v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    @Value("${spring.application.name:time-logging-service}")
    private String applicationName;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("TechTorque Time Logging Service API")
                        .version("1.0.0")
                        .description(
                            "REST API for employee time tracking and work hour logging. " +
                            "This service enables employees to log time spent on services and projects, " +
                            "track work progress, and generate summaries for productivity analysis.\n\n" +
                            "**Key Features:**\n" +
                            "- Log work hours for services and projects\n" +
                            "- Query time logs by employee, date range, service, or project\n" +
                            "- Generate daily and weekly summaries\n" +
                            "- Track employee productivity and work distribution\n" +
                            "- Role-based access control (EMPLOYEE, ADMIN)\n\n" +
                            "**Authentication:**\n" +
                            "All endpoints require JWT authentication via the API Gateway. " +
                            "Include the bearer token in the Authorization header."
                        )
                        .contact(new Contact()
                                .name("TechTorque Development Team")
                                .email("dev@techtorque.com")
                                .url("https://techtorque.com"))
                        .license(new License()
                                .name("Proprietary")
                                .url("https://techtorque.com/license"))
                )
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8085")
                                .description("Local development server"),
                        new Server()
                                .url("http://localhost:8080/api/v1")
                                .description("Local API Gateway"),
                        new Server()
                                .url("https://api.techtorque.com/v1")
                                .description("Production API Gateway")
                ))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .name("bearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Enter JWT token obtained from the authentication service")
                        )
                );
    }
}
