package com.lest.job.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.lest.common.core.utils.StringUtils;

/**
 * 定时任务调度示例任务类<br>
 * 可通过 Spring TaskScheduler 调用此类中的方法。
 *
 * @author yshan2028
 */
@Component("ryTask")
public class RyTask
{
    private static final Logger log = LoggerFactory.getLogger(RyTask.class);

    public void ryMultipleParams(String s, Boolean b, Long l, Double d, Integer i)
    {
        log.info("执行多参方法：字符串类型={}，布尔类型={}，长整型={}，浮点型={}，整型={}", s, b, l, d, i);
    }

    public void ryParams(String params)
    {
        log.info("执行有参方法：{}", params);
    }

    public void ryNoParams()
    {
        log.info("执行无参方法");
    }
}
