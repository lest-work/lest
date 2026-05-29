package com.lest.modules.open;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 开放平台服务启动类
 */
@SpringBootApplication(scanBasePackages = {"com.lest.modules.open", "com.lest.common"})
@MapperScan("com.lest.modules.open.mapper")
public class LestOpenApplication {

    public static void main(String[] args) {
        SpringApplication.run(LestOpenApplication.class, args);
    }
}
