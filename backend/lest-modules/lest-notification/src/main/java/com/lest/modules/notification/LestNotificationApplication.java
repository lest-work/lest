package com.lest.modules.notification;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 通知服务启动类
 */
@SpringBootApplication(scanBasePackages = {"com.lest.modules.notification", "com.lest.common"})
@MapperScan("com.lest.modules.notification.mapper")
public class LestNotificationApplication {

    public static void main(String[] args) {
        SpringApplication.run(LestNotificationApplication.class, args);
    }
}
