package com.lest.openapi.mapper;

import com.lest.openapi.domain.WebhookEndpoint;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface WebhookEndpointMapper
{
    List<WebhookEndpoint> selectWebhookList(WebhookEndpoint webhook);
    WebhookEndpoint selectWebhookById(Long webhookId);
    List<WebhookEndpoint> selectByEvent(String event);
    int insertWebhook(WebhookEndpoint webhook);
    int updateWebhook(WebhookEndpoint webhook);
    int deleteWebhookById(Long webhookId);
}
