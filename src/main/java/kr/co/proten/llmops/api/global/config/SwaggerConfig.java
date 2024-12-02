package kr.co.proten.llmops.api.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        // Create a SecurityScheme object
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP) // Set the type to HTTP
                .scheme("bearer") // Specify the scheme as Bearer
                .bearerFormat("JWT"); // Indicate the format is JWT

        // Create a SecurityRequirement object
        SecurityRequirement securityRequirement = new SecurityRequirement().addList("Authorization");

        // Build and return the OpenAPI object
        return new OpenAPI()
                .info(new Info()
                        .title("Proten LLM Ops! API Documentation")
                        .version("1.0")
                        .description("프로텐 llm ops (PRIME) API"))
                .components(new Components().addSecuritySchemes("Authorization", securityScheme))
                .addSecurityItem(securityRequirement);
    }
}