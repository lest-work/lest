package com.lest.modules.auth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * lest-auth 认证服务启动类
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@SpringBootApplication(scanBasePackages = {"com.lest.modules.auth", "com.lest.common"})
@MapperScan("com.lest.modules.auth.mapper")
@EnableDiscoveryClient
public class LestAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(LestAuthApplication.class, args);
    }
}
