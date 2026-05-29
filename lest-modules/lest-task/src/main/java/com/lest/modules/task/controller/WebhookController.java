package com.lest.modules.task.controller;

import com.lest.common.core.Result;
import com.lest.modules.task.entity.dto.CIBuildDTO;
import com.lest.modules.task.entity.dto.CommitDTO;
import com.lest.modules.task.service.WebhookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * Webhook控制器
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Slf4j
@RestController
@RequestMapping("/webhook")
public class WebhookController {

    private final WebhookService webhookService;

    public WebhookController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    /**
     * CI构建完成回调
     */
    @PostMapping("/ci/build")
    public Result<Void> ciBuild(@RequestBody CIBuildDTO dto) {
        log.info("收到CI构建回调: buildId={}, status={}", dto.getBuildId(), dto.getBuildStatus());
        webhookService.handleCIBuild(dto);
        return Result.ok();
    }

    /**
     * Git提交推送回调
     */
    @PostMapping("/git/commit")
    public Result<Void> gitCommit(@RequestBody CommitDTO dto) {
        log.info("收到Git提交回调: commitHash={}", dto.getCommitHash());
        webhookService.handleGitCommit(dto);
        return Result.ok();
    }
}
