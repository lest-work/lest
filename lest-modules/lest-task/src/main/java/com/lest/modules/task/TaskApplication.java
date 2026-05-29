package com.lest.modules.task;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * lest-task 任务服务启动类
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@SpringBootApplication(scanBasePackages = {"com.lest.modules.task", "com.lest.common"})
@MapperScan("com.lest.modules.task.mapper")
public class TaskApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskApplication.class, args);
    }
}
