package com.lest.modules.ai;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * AI 服务启动类
 */
@SpringBootApplication(scanBasePackages = {"com.lest.modules.ai", "com.lest.common"})
@MapperScan("com.lest.modules.ai.mapper")
public class LestAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(LestAiApplication.class, args);
    }
}
