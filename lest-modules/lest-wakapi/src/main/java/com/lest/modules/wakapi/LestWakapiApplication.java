package com.lest.modules.wakapi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * WakaTime 服务启动类
 */
@SpringBootApplication(scanBasePackages = {"com.lest.modules.wakapi", "com.lest.common"})
@MapperScan("com.lest.modules.wakapi.mapper")
public class LestWakapiApplication {

    public static void main(String[] args) {
        SpringApplication.run(LestWakapiApplication.class, args);
    }
}
