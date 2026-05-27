package com.lest.modules.file;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 文件服务启动类
 */
@SpringBootApplication(scanBasePackages = {"com.lest.modules.file", "com.lest.common"})
@MapperScan("com.lest.modules.file.mapper")
public class LestFileApplication {

    public static void main(String[] args) {
        SpringApplication.run(LestFileApplication.class, args);
    }
}
