package com.lest.common.redis.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Redis 配置
 * <p>
 * 提供两套 Redis 客户端：
 * <ul>
 *   <li><b>StringRedisTemplate</b> — Spring Data Redis 内置，简化 String/JSON 缓存操作</li>
 *   <li><b>RedissonClient</b> — Redisson 开源库，提供分布式锁、信号量、限流等高级能力</li>
 * </ul>
 */
@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host:localhost}")
    private String host;

    @Value("${spring.data.redis.port:6379}")
    private int port;

    @Value("${spring.data.redis.password:}")
    private String password;

    @Value("${spring.data.redis.database:0}")
    private int database;

    /**
     * Lettuce 连接工厂，支持连接池
     */
    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(host);
        config.setPort(port);
        config.setDatabase(database);
        if (password != null && !password.isBlank()) {
            config.setPassword(password);
        }

        LettucePoolingClientConfiguration poolConfig = LettucePoolingClientConfiguration.builder()
                .poolConfig(new org.apache.commons.pool2.impl.GenericObjectPoolConfig<>())
                .build();

        return new LettuceConnectionFactory(config, poolConfig);
    }

    /**
     * StringRedisTemplate — 字符串/JSON 缓存
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }

    /**
     * RedissonClient — 分布式锁、分布式集合、分布式限流
     * <p>
     * 使用单节点模式部署。在生产环境中建议改为集群/哨兵模式配置。
     * </p>
     */
    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient() {
        Config config = new Config();
        String addr = "redis://" + host + ":" + port;
        config.useSingleServer()
                .setAddress(addr)
                .setDatabase(database)
                .setPassword(password.isBlank() ? null : password)
                .setConnectionPoolSize(64)
                .setConnectionMinimumIdleSize(16)
                .setConnectTimeout(10000)
                .setTimeout(3000)
                .setRetryAttempts(3)
                .setRetryInterval(1500);
        return Redisson.create(config);
    }
}
