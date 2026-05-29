package com.lest.modules.job.service.impl;

import com.lest.modules.job.entity.domain.SysJobLog;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * Quartz 任务执行器
 *
 * @author yshan2028
 */
@Slf4j
public class QuartzJobExecutor implements Job {

    private static final String JOB_DATA_KEY = "JOB_DATA_KEY";

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        SysJobLog jobLog = new SysJobLog();
        jobLog.setCreatedAt(LocalDateTime.now());
        try {
            JobDataMap dataMap = context.getJobDetail().getJobDataMap();
            String invokeTarget = dataMap.getString(JOB_DATA_KEY);

            jobLog.setJobId(Long.parseLong(context.getJobDetail().getKey().getName()));
            jobLog.setJobGroup(context.getJobDetail().getKey().getGroup());
            jobLog.setInvokeTarget(invokeTarget);
            jobLog.setStartTime(LocalDateTime.now());

            // 执行目标方法
            String result = invokeTarget(invokeTarget);
            jobLog.setJobMessage("执行成功");
            jobLog.setStatus(0);
            log.info("定时任务执行成功: {}", invokeTarget);
        } catch (Exception e) {
            jobLog.setJobMessage("执行失败: " + e.getMessage());
            jobLog.setStatus(1);
            jobLog.setExceptionInfo(e.toString());
            log.error("定时任务执行失败: {}", context.getJobDetail().getKey(), e);
        } finally {
            jobLog.setStopTime(LocalDateTime.now());
            // 异步保存日志
            saveJobLogAsync(jobLog);
        }
    }

    private String invokeTarget(String invokeTarget) throws Exception {
        if (!StringUtils.hasText(invokeTarget)) {
            throw new RuntimeException("调用目标为空");
        }

        // 支持两种格式:
        // 1. beanName.methodName()  — Spring Bean 方法调用
        // 2. com.xxx.Class.method() — Java 类静态方法调用

        if (invokeTarget.contains(".")) {
            String[] parts = invokeTarget.split("\\.");
            String methodName = parts[parts.length - 1];
            String classOrBeanName = invokeTarget.substring(0, invokeTarget.length() - methodName.length() - 1);

            ApplicationContext ctx = SpringContextHolder.getApplicationContext();

            if (classOrBeanName.contains(".")) {
                // Java 类静态方法
                Class<?> clazz = Class.forName(classOrBeanName);
                Method method = clazz.getMethod(methodName);
                Object result = method.invoke(null);
                return result != null ? result.toString() : "null";
            } else {
                // Spring Bean 方法
                Object bean = ctx.getBean(classOrBeanName);
                Method method = bean.getClass().getMethod(methodName);
                Object result = method.invoke(bean);
                return result != null ? result.toString() : "null";
            }
        }

        throw new RuntimeException("无效的调用目标格式: " + invokeTarget);
    }

    private void saveJobLogAsync(SysJobLog jobLog) {
        try {
            SpringContextHolder.getBean(com.lest.modules.job.mapper.SysJobLogMapper.class)
                    .insert(jobLog);
        } catch (Exception e) {
            log.error("保存任务日志失败", e);
        }
    }
}
