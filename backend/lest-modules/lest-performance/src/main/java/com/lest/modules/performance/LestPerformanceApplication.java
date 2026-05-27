package com.lest.modules.performance;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 绩效服务启动类
 */
@SpringBootApplication(scanBasePackages = {"com.lest.modules.performance", "com.lest.common"})
@MapperScan("com.lest.modules.performance.mapper")
public class LestPerformanceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LestPerformanceApplication.class, args);
    }
}
