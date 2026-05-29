package com.lest.modules.file;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication(scanBasePackages = {"com.lest.modules.file", "com.lest.common"})
@MapperScan("com.lest.modules.file.mapper")
@ConfigurationPropertiesScan("com.lest.modules.file.config")
public class LestFileApplication {

    public static void main(String[] args) {
        SpringApplication.run(LestFileApplication.class, args);
    }
}
