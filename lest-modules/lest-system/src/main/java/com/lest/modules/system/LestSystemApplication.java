package com.lest.modules.system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 系统管理服务启动类
 */
@SpringBootApplication(scanBasePackages = {"com.lest.modules.system", "com.lest.common"})
@MapperScan("com.lest.modules.system.mapper")
@EnableDiscoveryClient
public class LestSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(LestSystemApplication.class, args);
    }
}
