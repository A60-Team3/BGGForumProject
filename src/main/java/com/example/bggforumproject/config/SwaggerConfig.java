package com.example.bggforumproject.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        tags = {
                @Tag(name = "tags", description = "Tag related endpoints. Authentication required."),
                @Tag(name = "users", description = "User related endpoints. Authentication required.")
        }
)
public class SwaggerConfig {

    @Bean
    public OpenAPI OpenApi() {
        return new OpenAPI()
                .info(new Info().title("BoardGameGeeks Forum System API")
                        .version("V1")
                        .description("This is our team project for sample ForumSystem")
                        .contact(new Contact().name("Milan Statev & Asen Momchev")
                                .url("https://github.com/A60-Team3/BGGForumProject"))
                        .termsOfService("Terms of Service"))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .name("bearerAuth")
                                .description("JWT authentication")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/**")
                .build();
    }
}
