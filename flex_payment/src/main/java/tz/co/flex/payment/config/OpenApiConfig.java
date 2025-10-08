package tz.co.flex.payment.config;

import io.swagger.v3.oas.models.Components;
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

@Configuration
public class OpenApiConfig {

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        final String apiTitle = "Flex Payment API";
        
        return new OpenAPI()
                .servers(getServers())
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        securitySchemeName,
                                        new SecurityScheme()
                                                .name(securitySchemeName)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                                .description("JWT Authorization header using the Bearer scheme")
                                )
                )
                .info(new Info()
                        .title(apiTitle)
                        .description("""
                                <h2>Flex Payment System API Documentation</h2>
                                <p>This page documents the RESTful web services for the Flex Payment System.</p>
                                <p><strong>Environment:</strong> %s</p>
                                """.formatted(activeProfile.toUpperCase()))
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Flex Support")
                                .email("support@flex.co.tz")
                                .url("https://flex.co.tz"))
                        .license(new License()
                                .name("Proprietary")
                                .url("https://flex.co.tz/terms"))
                );
    }
    
    private List<Server> getServers() {
        Server devServer = new Server()
                .url("http://localhost:8080")
                .description("Development Server");

        return List.of(devServer);
    }
}
