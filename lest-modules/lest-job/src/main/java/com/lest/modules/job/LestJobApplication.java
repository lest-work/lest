package com.lest.modules.job;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 定时任务服务
 *
 * @author yshan2028
 */
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.lest.**.feign")
@SpringBootApplication(scanBasePackages = "com.lest")
public class LestJobApplication {

    public static void main(String[] args) {
        SpringApplication.run(LestJobApplication.class, args);
    }
}
