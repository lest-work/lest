package com.lest.gateway.config;

import com.lest.gateway.handler.GatewayExceptionHandler;
import com.lest.gateway.handler.SentinelFallbackHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.server.WebExceptionHandler;

/**
 * 网关异常处理器配置
 */
@Configuration
@EnableConfigurationProperties
public class GatewayHandlerConfig {

    @Bean
    @Order(-1)
    public WebExceptionHandler gatewayExceptionHandler() {
        return new GatewayExceptionHandler();
    }

    @Bean
    @Order(0)
    public WebExceptionHandler sentinelFallbackHandler() {
        return new SentinelFallbackHandler();
    }
}
