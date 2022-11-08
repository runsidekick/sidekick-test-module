package com.runsidekick.testmode.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    private static final String SCHEME_NAME = "bearerScheme";
    private static final String SCHEME = "Bearer";

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("sidekick-test-mode-api")
                .pathsToMatch("/api/**")
                .build();
    }

    @Bean
    public OpenAPI springShopOpenAPI() {
        OpenAPI openAPI = new OpenAPI()
                .info(new Info().title("Sidekick Test Mode API")
                        .description("Sidekick Test Mode API")
                        .version("v0.0.1")
                        .contact(new Contact().name("Sidekick")
                                .url("https://www.runsidekick.com/company")
                                .email("info@runsidekick.com")));
        addSecurity(openAPI);
        return openAPI;
    }

    private void addSecurity(OpenAPI openApi) {
        Components components = createComponents();
        SecurityRequirement securityItem = new SecurityRequirement().addList(SCHEME_NAME);

        openApi.components(components).addSecurityItem(securityItem);
    }

    private Components createComponents() {
        var components = new Components();
        components.addSecuritySchemes(SCHEME_NAME, createSecurityScheme());

        return components;
    }

    private SecurityScheme createSecurityScheme() {
        return new SecurityScheme()
                .name(SCHEME_NAME)
                .type(SecurityScheme.Type.HTTP)
                .scheme(SCHEME);
    }
}