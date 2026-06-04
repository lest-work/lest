package com.lest.common.mq.consumer;

import com.lest.common.mq.annotation.LestKafkaListener;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @LestKafkaListener 注解处理器<br>
 * 扫描所有带有 @LestKafkaListener 注解的方法，自动注册 Kafka 消费者容器。
 *
 * @author yshan2028
 */
@Component
public class LestKafkaListenerBeanPostProcessor implements BeanPostProcessor
{
    private static final Logger log = LoggerFactory.getLogger(LestKafkaListenerBeanPostProcessor.class);

    @Autowired(required = false)
    private ConsumerFactory<String, String> consumerFactory;

    private final Map<Object, List<ConsumerMethod>> consumerMethods = new HashMap<>();

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException
    {
        for (Method method : bean.getClass().getDeclaredMethods())
        {
            if (method.isAnnotationPresent(LestKafkaListener.class))
            {
                if (!method.getReturnType().equals(Void.TYPE) && !method.getReturnType().equals(void.class))
                {
                    log.warn("@LestKafkaListener method {} should return void, skipping", method.getName());
                    continue;
                }
                consumerMethods.computeIfAbsent(bean, k -> new ArrayList<>())
                    .add(new ConsumerMethod(bean, method));
                log.info("[LestKafkaListener] Found listener method: {}.{}", bean.getClass().getSimpleName(), method.getName());
            }
        }
        return bean;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException
    {
        return bean;
    }

    public record ConsumerMethod(Object bean, Method method) {}

    public Map<Object, List<ConsumerMethod>> getConsumerMethods()
    {
        return consumerMethods;
    }
}
