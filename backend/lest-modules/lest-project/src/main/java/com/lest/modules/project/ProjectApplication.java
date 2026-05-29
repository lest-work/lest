package com.lest.modules.project;

import com.lest.common.security.annotation.EnableCustomConfig;
import com.lest.common.security.annotation.EnableLestFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 项目管理模块启动类
 */
@EnableCustomConfig
@EnableLestFeignClients
@SpringBootApplication
public class ProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectApplication.class, args);
    }
}
