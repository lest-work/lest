package com.lest.common.mq.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lest.common.mq.core.LestPlatformEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Kafka 消息消费者基类<br>
 * 提供标准化的消息处理流程：反序列化 -> 事件路由 -> 异常处理。
 *
 * <p>子类只需覆写 handle 方法即可处理特定事件类型：</p>
 * <pre>
 * {@code
 * @Component
 * public class TaskEventConsumer extends KafkaConsumerBase {
 *     public TaskEventConsumer(ObjectMapper objectMapper) {
 *         super(objectMapper);
 *     }
 *
 *     @Override
 *     protected void handle(LestPlatformEvent event) {
 *         switch (event.getEventType()) {
 *             case "task.assigned" -> processTaskAssigned(event);
 *             case "task.commented" -> processTaskCommented(event);
 *         }
 *     }
 * }
 * }
 * </pre>
 *
 * @author yshan2028
 */
public abstract class KafkaConsumerBase
{
    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerBase.class);

    protected final ObjectMapper objectMapper;

    protected KafkaConsumerBase(ObjectMapper objectMapper)
    {
        this.objectMapper = objectMapper;
    }

    /**
     * 处理原始 JSON 消息字符串
     */
    public void onMessage(String message)
    {
        if (message == null || message.isBlank())
        {
            log.warn("[KafkaConsumer] Received empty message, ignoring");
            return;
        }

        try
        {
            LestPlatformEvent event = objectMapper.readValue(message, LestPlatformEvent.class);
            log.debug("[KafkaConsumer] Received event: type={}, id={}", event.getEventType(), event.getEventId());
            handle(event);
        }
        catch (Exception e)
        {
            log.error("[KafkaConsumer] Failed to deserialize message: {}", e.getMessage());
            onDeserializationError(message, e);
        }
    }

    /**
     * 处理平台事件（由子类实现）
     */
    protected abstract void handle(LestPlatformEvent event);

    /**
     * 反序列化失败时的回调（可覆写）
     */
    protected void onDeserializationError(String rawMessage, Exception e)
    {
        log.error("[KafkaConsumer] Raw message: {}", rawMessage);
    }

    /**
     * 辅助方法：从事件中获取 Long 类型的字段
     */
    protected Long getLong(LestPlatformEvent event, String key)
    {
        Object value = event.getPayload().get(key);
        if (value == null) { return null; }
        if (value instanceof Number) { return ((Number) value).longValue(); }
        if (value instanceof String) { return Long.parseLong((String) value); }
        return null;
    }

    /**
     * 辅助方法：从事件中获取 String 类型的字段
     */
    protected String getString(LestPlatformEvent event, String key)
    {
        Object value = event.getPayload().get(key);
        return value != null ? String.valueOf(value) : null;
    }

    /**
     * 辅助方法：从事件中获取 Integer 类型的字段
     */
    protected Integer getInt(LestPlatformEvent event, String key)
    {
        Object value = event.getPayload().get(key);
        if (value == null) { return null; }
        if (value instanceof Number) { return ((Number) value).intValue(); }
        if (value instanceof String) { return Integer.parseInt((String) value); }
        return null;
    }
}
