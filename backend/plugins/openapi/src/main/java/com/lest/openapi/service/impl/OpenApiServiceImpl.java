package com.lest.openapi.service.impl;

import com.lest.openapi.domain.ApiKey;
import com.lest.openapi.domain.WebhookEndpoint;
import com.lest.openapi.mapper.ApiKeyMapper;
import com.lest.openapi.mapper.WebhookEndpointMapper;
import com.lest.openapi.service.IOpenApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Service
public class OpenApiServiceImpl implements IOpenApiService
{
    private static final SecureRandom RANDOM = new SecureRandom();

    @Autowired private ApiKeyMapper apiKeyMapper;
    @Autowired private WebhookEndpointMapper webhookMapper;

    @Override
    public List<ApiKey> selectApiKeyList(ApiKey apiKey)
    {
        return apiKeyMapper.selectApiKeyList(apiKey);
    }

    @Override
    public ApiKey createApiKey(ApiKey apiKey)
    {
        String rawKey = generateApiKey();
        String prefix = rawKey.substring(0, 8);
        apiKey.setApiKey(rawKey);
        apiKey.setKeyPrefix(prefix);
        apiKey.setIsEnabled(1);
        apiKey.setCreateAt(new Date());
        apiKeyMapper.insertApiKey(apiKey);
        return apiKey;
    }

    @Override
    public int revokeApiKey(Long keyId)
    {
        ApiKey key = new ApiKey();
        key.setKeyId(keyId);
        key.setIsEnabled(0);
        key.setUpdateAt(new Date());
        return apiKeyMapper.updateApiKey(key);
    }

    @Override
    public ApiKey validateApiKey(String apiKey)
    {
        ApiKey key = apiKeyMapper.selectApiKeyByValue(apiKey);
        if (key != null && key.getExpiresAt() != null && key.getExpiresAt().before(new Date()))
        {
            return null;
        }
        return key;
    }

    @Override
    public List<WebhookEndpoint> selectWebhookList(WebhookEndpoint webhook)
    {
        return webhookMapper.selectWebhookList(webhook);
    }

    @Override
    public WebhookEndpoint createWebhook(WebhookEndpoint webhook)
    {
        webhook.setSecret(generateApiKey().substring(0, 32));
        webhook.setIsEnabled(1);
        webhook.setCreateAt(new Date());
        webhookMapper.insertWebhook(webhook);
        return webhook;
    }

    @Override
    public int updateWebhook(WebhookEndpoint webhook)
    {
        webhook.setUpdateAt(new Date());
        return webhookMapper.updateWebhook(webhook);
    }

    @Override
    public int deleteWebhook(Long webhookId)
    {
        return webhookMapper.deleteWebhookById(webhookId);
    }

    @Override
    public List<WebhookEndpoint> getWebhooksForEvent(String event)
    {
        return webhookMapper.selectByEvent(event);
    }

    private String generateApiKey()
    {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return "LEST-" + Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
