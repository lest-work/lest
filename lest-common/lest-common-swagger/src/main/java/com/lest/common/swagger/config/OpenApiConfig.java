package com.lest.common.swagger.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI 3.0 全局配置
 *
 * @author yshan2028
 */
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(SwaggerProperties.class)
public class OpenApiConfig {

    private final SwaggerProperties properties;

    @Bean
    public OpenAPI lestPlatformOpenAPI() {
        Contact contact = new Contact()
                .name(properties.getContactName())
                .email(properties.getContactEmail())
                .url(properties.getContactUrl());

        License license = new License()
                .name(properties.getLicenseName())
                .url(properties.getLicenseUrl());

        Info info = new Info()
                .title(properties.getTitle())
                .description(properties.getDescription())
                .version(properties.getVersion())
                .contact(contact)
                .license(license);

        return new OpenAPI()
                .info(info);
    }
}
