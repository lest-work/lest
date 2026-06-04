package com.lest.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.lest.common.security.annotation.EnableLestFeignClients;

@EnableLestFeignClients
@SpringBootApplication
public class LestNotificationApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(LestNotificationApplication.class, args);
    }
}
