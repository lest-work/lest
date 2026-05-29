package com.lest.modules.auth.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI lestPlatformOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8096");
        devServer.setDescription("Development Server");

        Contact contact = new Contact();
        contact.setName("LEST Platform Team");
        contact.setEmail("lest@example.com");

        License license = new License()
                .name("Apache 2.0")
                .url("https://www.apache.org/licenses/LICENSE-2.0");

        Info info = new Info()
                .title("LEST Platform API")
                .version("1.0.0")
                .description("LEST Platform - AI-Native Agile Management Platform")
                .contact(contact)
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer));
    }
}
