package com.lest.job;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.lest.common.security.annotation.EnableCustomConfig;
import com.lest.common.security.annotation.EnableLestFeignClients;

/**
 * 定时任务
 * 
 * @author yshan2028
 */
@EnableCustomConfig
@EnableLestFeignClients   
@SpringBootApplication
public class LestJobApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(LestJobApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  定时任务模块启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
                " .-------.       ____     __        \n" +
                " |  _ _   \\      \\   \\   /  /    \n" +
                " | ( ' )  |       \\  _. /  '       \n" +
                " |(_ o _) /        _( )_ .'         \n" +
                " | (_,_).' __  ___(_ o _)'          \n" +
                " |  |\\ \\  |  ||   |(_,_)'         \n" +
                " |  | \\ `'   /|   `-'  /           \n" +
                " |  |  \\    /  \\      /           \n" +
                " ''-'   `'-'    `-..-'              ");
    }
}
