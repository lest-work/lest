package com.lest.common.mq.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

/**
 * Kafka 消息发送器
 */
public class KafkaMessageSender
{
    private static final Logger log = LoggerFactory.getLogger(KafkaMessageSender.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaMessageSender(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper)
    {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void send(String topic, String key, Object payload)
    {
        try
        {
            String json = objectMapper.writeValueAsString(payload);
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, json);
            kafkaTemplate.send(record).whenComplete((result, ex) -> {
                if (ex != null)
                {
                    log.error("Failed to send message to topic={}, key={}", topic, key, ex);
                }
                else
                {
                    RecordMetadata meta = result.getRecordMetadata();
                    log.debug("Message sent to topic={}, partition={}, offset={}",
                            meta.topic(), meta.partition(), meta.offset());
                }
            });
        }
        catch (Exception e)
        {
            log.error("Failed to serialize message payload for topic={}", topic, e);
        }
    }

    public void send(String topic, Object payload)
    {
        send(topic, null, payload);
    }

    public CompletableFuture<SendResult<String, String>> sendSync(String topic, String key, Object payload) throws Exception
    {
        String json = objectMapper.writeValueAsString(payload);
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, json);
        return kafkaTemplate.send(record);
    }
}
