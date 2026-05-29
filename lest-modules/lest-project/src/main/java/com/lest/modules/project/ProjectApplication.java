package com.lest.modules.project;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 项目管理模块启动类
 */
@SpringBootApplication(scanBasePackages = {"com.lest.modules.project", "com.lest.common"})
@MapperScan("com.lest.modules.project.mapper")
public class ProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectApplication.class, args);
    }
}
