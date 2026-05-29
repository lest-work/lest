package com.lest.modules.task.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.lest.common.core.web.controller.BaseController;
import com.lest.common.core.web.domain.AjaxResult;
import com.lest.modules.task.domain.TaskCommit;
import com.lest.modules.task.service.IWebhookService;

/**
 * Webhook回调
 * 
 * @author yshan2028
 */
@RestController
@RequestMapping("/webhook")
public class WebhookController extends BaseController
{
    @Autowired
    private IWebhookService webhookService;

    /**
     * CI构建完成回调
     */
    @PostMapping("/ci/build")
    public AjaxResult ciBuild(@RequestBody TaskCommit commit)
    {
        webhookService.handleCIBuild(commit);
        return success();
    }

    /**
     * Git提交推送回调
     */
    @PostMapping("/git/commit")
    public AjaxResult gitCommit(@RequestBody TaskCommit commit)
    {
        webhookService.handleGitCommit(commit);
        return success();
    }
}
