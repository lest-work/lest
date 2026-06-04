package com.lest.task.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.lest.task.domain.Task;
import com.lest.task.domain.TaskCommit;
import com.lest.task.mapper.TaskCommitMapper;
import com.lest.task.mapper.TaskMapper;
import com.lest.task.service.IWebhookService;

/**
 * Webhook 服务层实现
 * 
 * @author yshan2028
 */
@Service
public class WebhookServiceImpl implements IWebhookService
{
    private static final Logger log = LoggerFactory.getLogger(WebhookServiceImpl.class);

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private TaskCommitMapper commitMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleCIBuild(TaskCommit commit)
    {
        log.info("处理CI构建回调: commitHash={}", commit.getCommitHash());
        if (commit.getCommitHash() == null)
        {
            log.warn("CI构建回调缺少commitHash");
            return;
        }
        Long taskId = commitMapper.selectTaskIdByCommitHash(commit.getCommitHash());
        if (taskId == null)
        {
            log.info("未找到关联任务: commitHash={}", commit.getCommitHash());
            return;
        }
        Task task = taskMapper.selectById(taskId);
        if (task == null)
        {
            log.warn("关联任务不存在: taskId={}", taskId);
            return;
        }
        if ("todo".equals(task.getStatus()))
        {
            task.setStatus("in_progress");
            taskMapper.updateById(task);
            log.info("CI构建成功，自动更新任务状态: taskId={}", taskId);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleGitCommit(TaskCommit commit)
    {
        log.info("处理Git提交回调: commitHash={}", commit.getCommitHash());
        if (commit.getCommitHash() == null)
        {
            log.warn("Git提交回调缺少commitHash");
            return;
        }
        Long taskId = commitMapper.selectTaskIdByCommitHash(commit.getCommitHash());
        if (taskId != null)
        {
            log.info("提交已存在关联: commitHash={}, taskId={}", commit.getCommitHash(), taskId);
            return;
        }
        log.info("Git提交已记录，待后续关联任务: commitHash={}", commit.getCommitHash());
    }
}
