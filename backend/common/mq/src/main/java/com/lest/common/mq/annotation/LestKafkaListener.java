package com.lest.common.mq.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LestKafkaListener
{
    /**
     * Kafka Topic 名称
     */
    String topic();

    /**
     * 消费者组 ID（默认使用 topic 名称作为 groupId）
     */
    String groupId() default "";

    /**
     * 是否启用（默认 true）
     */
    boolean enabled() default true;

    /**
     * 消费者并发数（默认 1）
     */
    int concurrency() default 1;

    /**
     * 是否自动提交 offset（默认 true）
     */
    boolean autoCommit() default true;
}
