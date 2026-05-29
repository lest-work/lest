package com.lest.gateway;

import com.lest.gateway.config.properties.GatewayAuthProperties;
import com.lest.gateway.config.properties.IgnoreWhiteProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@EnableConfigurationProperties({GatewayAuthProperties.class, IgnoreWhiteProperties.class})
public class LestGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(LestGatewayApplication.class, args);
    }
}
