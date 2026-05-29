package com.lest.modules.job.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lest.common.core.PageResult;
import com.lest.modules.job.entity.domain.SysJob;
import com.lest.modules.job.entity.domain.SysJobLog;
import com.lest.modules.job.mapper.SysJobLogMapper;
import com.lest.modules.job.mapper.SysJobMapper;
import com.lest.modules.job.service.JobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 定时任务服务实现
 *
 * @author yshan2028
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final SysJobMapper jobMapper;
    private final SysJobLogMapper jobLogMapper;
    private final Scheduler scheduler;

    private static final String JOB_DATA_KEY = "JOB_DATA_KEY";

    @Override
    public PageResult<SysJob> page(Integer page, Integer size, String jobName, String jobGroup, Integer status) {
        Page<SysJob> p = new Page<>(page, size);
        LambdaQueryWrapper<SysJob> w = new LambdaQueryWrapper<>();
        w.like(jobName != null, SysJob::getJobName, jobName);
        w.eq(jobGroup != null, SysJob::getJobGroup, jobGroup);
        w.eq(status != null, SysJob::getStatus, status);
        w.orderByDesc(SysJob::getCreatedAt);
        IPage<SysJob> result = jobMapper.selectPage(p, w);
        return PageResult.of(result.getRecords(), result.getTotal(), (int) result.getCurrent(), (int) result.getSize());
    }

    @Override
    public List<SysJob> list(SysJob job) {
        LambdaQueryWrapper<SysJob> w = new LambdaQueryWrapper<>();
        w.like(job.getJobName() != null, SysJob::getJobName, job.getJobName());
        w.eq(job.getJobGroup() != null, SysJob::getJobGroup, job.getJobGroup());
        w.eq(job.getStatus() != null, SysJob::getStatus, job.getStatus());
        w.orderByDesc(SysJob::getCreatedAt);
        return jobMapper.selectList(w);
    }

    @Override
    public SysJob getById(Long jobId) {
        return jobMapper.selectById(jobId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long add(SysJob job) {
        jobMapper.insert(job);
        addJobToScheduler(job);
        return job.getJobId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SysJob job) {
        removeJobFromScheduler(job.getJobId(), job.getJobGroup());
        jobMapper.updateById(job);
        if (job.getStatus() == 0) {
            addJobToScheduler(job);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long[] jobIds) {
        for (Long jobId : jobIds) {
            SysJob job = jobMapper.selectById(jobId);
            if (job != null) {
                removeJobFromScheduler(jobId, job.getJobGroup());
                jobMapper.deleteById(jobId);
            }
        }
    }

    @Override
    public void run(Long jobId) {
        SysJob job = jobMapper.selectById(jobId);
        if (job == null) {
            throw new RuntimeException("任务不存在");
        }
        try {
            JobKey jobKey = JobKey.jobKey(String.valueOf(jobId), job.getJobGroup());
            scheduler.triggerJob(jobKey);
        } catch (SchedulerException e) {
            log.error("立即执行任务失败: {}", jobId, e);
            throw new RuntimeException("立即执行任务失败: " + e.getMessage());
        }
    }

    @Override
    public void pause(Long jobId) {
        SysJob job = jobMapper.selectById(jobId);
        if (job == null) {
            throw new RuntimeException("任务不存在");
        }
        try {
            JobKey jobKey = JobKey.jobKey(String.valueOf(jobId), job.getJobGroup());
            scheduler.pauseJob(jobKey);
            job.setStatus(1);
            jobMapper.updateById(job);
        } catch (SchedulerException e) {
            log.error("暂停任务失败: {}", jobId, e);
            throw new RuntimeException("暂停任务失败: " + e.getMessage());
        }
    }

    @Override
    public void resume(Long jobId) {
        SysJob job = jobMapper.selectById(jobId);
        if (job == null) {
            throw new RuntimeException("任务不存在");
        }
        try {
            JobKey jobKey = JobKey.jobKey(String.valueOf(jobId), job.getJobGroup());
            scheduler.resumeJob(jobKey);
            job.setStatus(0);
            jobMapper.updateById(job);
        } catch (SchedulerException e) {
            log.error("恢复任务失败: {}", jobId, e);
            throw new RuntimeException("恢复任务失败: " + e.getMessage());
        }
    }

    @Override
    public PageResult<SysJobLog> logPage(Integer page, Integer size, Long jobId) {
        Page<SysJobLog> p = new Page<>(page, size);
        LambdaQueryWrapper<SysJobLog> w = new LambdaQueryWrapper<>();
        w.eq(jobId != null, SysJobLog::getJobId, jobId);
        w.orderByDesc(SysJobLog::getCreatedAt);
        IPage<SysJobLog> result = jobLogMapper.selectPage(p, w);
        return PageResult.of(result.getRecords(), result.getTotal(), (int) result.getCurrent(), (int) result.getSize());
    }

    @Override
    public void cleanLog() {
        jobLogMapper.delete(null);
    }

    // --- Private helper methods ---

    private void addJobToScheduler(SysJob job) {
        try {
            Class<? extends Job> jobClass = QuartzJobExecutor.class;
            JobDetail jobDetail = JobBuilder.newJob(jobClass)
                    .withIdentity(String.valueOf(job.getJobId()), job.getJobGroup())
                    .withDescription(job.getJobName())
                    .usingJobData(JOB_DATA_KEY, job.getInvokeTarget())
                    .storeDurably()
                    .build();

            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(String.valueOf(job.getJobId()), job.getJobGroup())
                    .withDescription(job.getJobName())
                    .withSchedule(CronScheduleBuilder.cronSchedule(job.getCronExpression())
                            .withMisfireHandlingInstructionFireAndProceed())
                    .build();

            scheduler.scheduleJob(jobDetail, trigger);
            if (job.getStatus() == 0) {
                scheduler.resumeJob(JobKey.jobKey(String.valueOf(job.getJobId()), job.getJobGroup()));
            }
            log.info("定时任务注册成功: {} ({})", job.getJobName(), job.getCronExpression());
        } catch (SchedulerException e) {
            log.error("定时任务注册失败: {}", job.getJobName(), e);
            throw new RuntimeException("定时任务注册失败: " + e.getMessage());
        }
    }

    private void removeJobFromScheduler(Long jobId, String jobGroup) {
        try {
            JobKey jobKey = JobKey.jobKey(String.valueOf(jobId), jobGroup);
            scheduler.deleteJob(jobKey);
        } catch (SchedulerException e) {
            log.warn("删除定时任务失败: {} {}", jobId, jobGroup, e);
        }
    }
}
