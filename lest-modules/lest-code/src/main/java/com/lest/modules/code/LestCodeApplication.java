package com.lest.modules.code;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 代码管理服务启动类
 */
@SpringBootApplication(scanBasePackages = {"com.lest.modules.code", "com.lest.common"})
@MapperScan("com.lest.modules.code.mapper")
public class LestCodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(LestCodeApplication.class, args);
    }
}
