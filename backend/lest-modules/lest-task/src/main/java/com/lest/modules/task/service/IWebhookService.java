package com.lest.modules.task.service;

import com.lest.modules.task.domain.TaskCommit;

/**
 * Webhook 服务层
 * 
 * @author yshan2028
 */
public interface IWebhookService
{
    /**
     * 处理CI构建完成回调
     */
    public void handleCIBuild(TaskCommit commit);

    /**
     * 处理Git提交推送回调
     */
    public void handleGitCommit(TaskCommit commit);
}
