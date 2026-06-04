package com.lest.release;

import com.lest.common.security.annotation.EnableCustomConfig;
import com.lest.common.security.annotation.EnableLestFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableCustomConfig
@EnableLestFeignClients
@SpringBootApplication
public class ReleaseApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReleaseApplication.class, args);
    }
}
