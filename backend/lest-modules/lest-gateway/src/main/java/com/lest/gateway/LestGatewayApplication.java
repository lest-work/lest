package com.lest.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class LestGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(LestGatewayApplication.class, args);
    }
}
