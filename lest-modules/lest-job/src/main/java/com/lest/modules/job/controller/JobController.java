package com.lest.modules.job.controller;

import com.lest.common.core.PageResult;
import com.lest.common.core.Result;
import com.lest.modules.job.entity.domain.SysJob;
import com.lest.modules.job.entity.domain.SysJobLog;
import com.lest.modules.job.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 定时任务控制器
 *
 * @author yshan2028
 */
@Slf4j
@RestController
@RequestMapping("/job")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    /**
     * 分页查询定时任务
     */
    @GetMapping("/page")
    public Result<PageResult<SysJob>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String jobName,
            @RequestParam(required = false) String jobGroup,
            @RequestParam(required = false) Integer status) {
        return Result.ok(jobService.page(page, size, jobName, jobGroup, status));
    }

    /**
     * 查询所有定时任务
     */
    @GetMapping("/list")
    public Result<List<SysJob>> list(SysJob job) {
        return Result.ok(jobService.list(job));
    }

    /**
     * 根据ID查询定时任务
     */
    @GetMapping("/{jobId}")
    public Result<SysJob> getById(@PathVariable Long jobId) {
        return Result.ok(jobService.getById(jobId));
    }

    /**
     * 新增定时任务
     */
    @PostMapping
    public Result<Long> add(@Valid @RequestBody SysJob job) {
        return Result.ok(jobService.add(job));
    }

    /**
     * 修改定时任务
     */
    @PutMapping
    public Result<Void> update(@Valid @RequestBody SysJob job) {
        jobService.update(job);
        return Result.ok();
    }

    /**
     * 删除定时任务
     */
    @DeleteMapping("/{jobIds}")
    public Result<Void> delete(@PathVariable Long[] jobIds) {
        jobService.delete(jobIds);
        return Result.ok();
    }

    /**
     * 立即执行一次定时任务
     */
    @PutMapping("/run/{jobId}")
    public Result<Void> run(@PathVariable Long jobId) {
        jobService.run(jobId);
        return Result.ok();
    }

    /**
     * 暂停定时任务
     */
    @PutMapping("/pause/{jobId}")
    public Result<Void> pause(@PathVariable Long jobId) {
        jobService.pause(jobId);
        return Result.ok();
    }

    /**
     * 恢复定时任务
     */
    @PutMapping("/resume/{jobId}")
    public Result<Void> resume(@PathVariable Long jobId) {
        jobService.resume(jobId);
        return Result.ok();
    }

    /**
     * 分页查询任务执行日志
     */
    @GetMapping("/log/page")
    public Result<PageResult<SysJobLog>> logPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long jobId) {
        return Result.ok(jobService.logPage(page, size, jobId));
    }

    /**
     * 清空任务执行日志
     */
    @DeleteMapping("/log/clean")
    public Result<Void> cleanLog() {
        jobService.cleanLog();
        return Result.ok();
    }
}
