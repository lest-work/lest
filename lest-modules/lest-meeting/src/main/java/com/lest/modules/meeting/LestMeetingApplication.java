package com.lest.modules.meeting;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 会议服务启动类
 */
@SpringBootApplication(scanBasePackages = {"com.lest.modules.meeting", "com.lest.common"})
@MapperScan("com.lest.modules.meeting.mapper")
public class LestMeetingApplication {

    public static void main(String[] args) {
        SpringApplication.run(LestMeetingApplication.class, args);
    }
}
