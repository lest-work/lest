package com.lest.openapi.service;

import com.lest.openapi.domain.ApiKey;
import com.lest.openapi.domain.WebhookEndpoint;
import java.util.List;

public interface IOpenApiService
{
    List<ApiKey> selectApiKeyList(ApiKey apiKey);
    ApiKey createApiKey(ApiKey apiKey);
    int revokeApiKey(Long keyId);
    ApiKey validateApiKey(String apiKey);
    List<WebhookEndpoint> selectWebhookList(WebhookEndpoint webhook);
    WebhookEndpoint createWebhook(WebhookEndpoint webhook);
    int updateWebhook(WebhookEndpoint webhook);
    int deleteWebhook(Long webhookId);
    List<WebhookEndpoint> getWebhooksForEvent(String event);
}
