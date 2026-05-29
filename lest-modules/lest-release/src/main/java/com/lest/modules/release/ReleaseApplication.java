package com.lest.modules.release;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = "com.lest")
@EnableDiscoveryClient
@MapperScan({"com.lest.modules.release.mapper", "com.lest.modules.auth.mapper"})
public class ReleaseApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReleaseApplication.class, args);
    }
}
