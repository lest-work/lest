package com.lest.modules.plugin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 插件系统启动类
 */
@SpringBootApplication(scanBasePackages = {"com.lest.modules.plugin", "com.lest.common"})
@MapperScan("com.lest.modules.plugin.mapper")
public class LestPluginApplication {

    public static void main(String[] args) {
        SpringApplication.run(LestPluginApplication.class, args);
    }
}
