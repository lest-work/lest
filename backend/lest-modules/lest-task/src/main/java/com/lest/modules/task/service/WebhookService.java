package com.lest.modules.task.service;

import com.lest.modules.task.entity.dto.CIBuildDTO;
import com.lest.modules.task.entity.dto.CommitDTO;

/**
 * Webhook服务接口
 *
 * @author Lest
 * @since 2026-05-26
 */
public interface WebhookService {

    /**
     * 处理CI构建完成回调
     */
    void handleCIBuild(CIBuildDTO dto);

    /**
     * 处理Git提交推送回调
     */
    void handleGitCommit(CommitDTO dto);
}
