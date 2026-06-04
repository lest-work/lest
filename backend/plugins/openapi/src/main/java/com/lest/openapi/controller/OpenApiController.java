package com.lest.openapi.controller;

import com.lest.common.core.web.controller.BaseController;
import com.lest.common.core.web.domain.AjaxResult;
import com.lest.common.core.web.page.TableDataInfo;
import com.lest.common.security.utils.SecurityUtils;
import com.lest.openapi.domain.ApiKey;
import com.lest.openapi.domain.WebhookEndpoint;
import com.lest.openapi.service.IOpenApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/openapi")
public class OpenApiController extends BaseController
{
    @Autowired
    private IOpenApiService openApiService;

    @GetMapping("/key/list")
    public TableDataInfo apiKeyList()
    {
        startPage();
        ApiKey query = new ApiKey();
        query.setUserId(SecurityUtils.getLoginUser().getUserid());
        List<ApiKey> list = openApiService.selectApiKeyList(query);
        return getDataTable(list);
    }

    @PostMapping("/key")
    public AjaxResult createApiKey(@RequestBody ApiKey apiKey)
    {
        apiKey.setUserId(SecurityUtils.getLoginUser().getUserid());
        ApiKey created = openApiService.createApiKey(apiKey);
        return success(created);
    }

    @DeleteMapping("/key/{keyId}")
    public AjaxResult revokeApiKey(@PathVariable Long keyId)
    {
        return toAjax(openApiService.revokeApiKey(keyId));
    }

    @GetMapping("/webhook/list")
    public TableDataInfo webhookList()
    {
        startPage();
        WebhookEndpoint query = new WebhookEndpoint();
        query.setUserId(SecurityUtils.getLoginUser().getUserid());
        List<WebhookEndpoint> list = openApiService.selectWebhookList(query);
        return getDataTable(list);
    }

    @PostMapping("/webhook")
    public AjaxResult createWebhook(@RequestBody WebhookEndpoint webhook)
    {
        webhook.setUserId(SecurityUtils.getLoginUser().getUserid());
        WebhookEndpoint created = openApiService.createWebhook(webhook);
        return success(created);
    }

    @PutMapping("/webhook")
    public AjaxResult updateWebhook(@RequestBody WebhookEndpoint webhook)
    {
        return toAjax(openApiService.updateWebhook(webhook));
    }

    @DeleteMapping("/webhook/{webhookId}")
    public AjaxResult deleteWebhook(@PathVariable Long webhookId)
    {
        return toAjax(openApiService.deleteWebhook(webhookId));
    }
}
