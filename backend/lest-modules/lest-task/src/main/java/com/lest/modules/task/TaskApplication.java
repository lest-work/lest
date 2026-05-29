package com.lest.modules.task;

import com.lest.common.security.annotation.EnableCustomConfig;
import com.lest.common.security.annotation.EnableLestFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * lest-task 任务服务启动类
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@EnableCustomConfig
@EnableLestFeignClients
@SpringBootApplication
public class TaskApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskApplication.class, args);
    }
}
