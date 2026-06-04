package com.lest.common.mq.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * LEST 平台事件监听器基类
 */
public class LestEventListener
{
    private static final Logger log = LoggerFactory.getLogger(LestEventListener.class);

    @Autowired
    protected ObjectMapper objectMapper;

    protected KafkaMessageSender sender;

    public LestEventListener() {}

    public LestEventListener(KafkaMessageSender sender)
    {
        this.sender = sender;
    }

    public void onMessage(ConsumerRecord<String, String> record)
    {
        try
        {
            Object payload = objectMapper.readValue(record.value(), LestPlatformEvent.class);
            handle(payload);
        }
        catch (Exception e)
        {
            log.error("Failed to process message from topic={}, offset={}",
                    record.topic(), record.offset(), e);
        }
    }

    protected void handle(Object event)
    {
        log.debug("Received event: {}", event);
    }
}
