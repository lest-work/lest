package com.lest.gateway.config;

import com.lest.gateway.config.properties.GatewayAuthProperties;
import com.lest.gateway.config.properties.IgnoreWhiteProperties;
import com.lest.gateway.filter.GatewayAuthFilter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 网关配置类 — 注册 GatewayAuthFilter 和配置属性
 *
 * @author yshan2028
 */
@Configuration
@EnableConfigurationProperties({GatewayAuthProperties.class, IgnoreWhiteProperties.class})
public class GatewayConfig {

    /**
     * 注册网关认证过滤器
     */
    @Bean
    public GatewayAuthFilter gatewayAuthFilter(GatewayAuthProperties authProperties) {
        return new GatewayAuthFilter(authProperties);
    }
}
