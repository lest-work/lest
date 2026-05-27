package com.lest.modules.task.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lest.common.base.Assert;
import com.lest.modules.task.common.ErrorCode;
import com.lest.modules.task.entity.domain.Task;
import com.lest.modules.task.entity.domain.TaskCommit;
import com.lest.modules.task.entity.dto.CIBuildDTO;
import com.lest.modules.task.entity.dto.CommitDTO;
import com.lest.modules.task.mapper.TaskCommitMapper;
import com.lest.modules.task.mapper.TaskMapper;
import com.lest.modules.task.service.WebhookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Webhook服务实现
 *
 * @author Lest
 * @since 2026-05-26
 */
@Slf4j
@Service
public class WebhookServiceImpl implements WebhookService {

    private final TaskMapper taskMapper;
    private final TaskCommitMapper commitMapper;

    public WebhookServiceImpl(TaskMapper taskMapper, TaskCommitMapper commitMapper) {
        this.taskMapper = taskMapper;
        this.commitMapper = commitMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleCIBuild(CIBuildDTO dto) {
        log.info("处理CI构建回调: buildId={}, status={}", dto.getBuildId(), dto.getBuildStatus());

        if (dto.getCommitHash() == null) {
            log.warn("CI构建回调缺少commitHash");
            return;
        }

        Long taskId = commitMapper.selectTaskIdByCommitHash(dto.getCommitHash());
        if (taskId == null) {
            log.info("未找到关联任务: commitHash={}", dto.getCommitHash());
            return;
        }

        Task task = taskMapper.selectById(taskId);
        if (task == null) {
            log.warn("关联任务不存在: taskId={}", taskId);
            return;
        }

        if ("success".equalsIgnoreCase(dto.getBuildStatus()) && "todo".equals(task.getStatus())) {
            task.setStatus("in_progress");
            taskMapper.updateById(task);
            log.info("CI构建成功，自动更新任务状态: taskId={}", taskId);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleGitCommit(CommitDTO dto) {
        log.info("处理Git提交回调: commitHash={}, repoId={}", dto.getCommitHash(), dto.getRepoId());

        if (dto.getCommitHash() == null) {
            log.warn("Git提交回调缺少commitHash");
            return;
        }

        Long taskId = commitMapper.selectTaskIdByCommitHash(dto.getCommitHash());
        if (taskId != null) {
            log.info("提交已存在关联: commitHash={}, taskId={}", dto.getCommitHash(), taskId);
            return;
        }

        log.info("Git提交已记录，待后续关联任务: commitHash={}", dto.getCommitHash());
    }
}
