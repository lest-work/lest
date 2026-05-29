package com.lest.common.datasource.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot4.autoconfigure.DruidDataSourceBuilder;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * Druid + 动态数据源配置
 *
 * @author yshan2028
 */
@Configuration
@EnableConfigurationProperties(DynamicDataSourceProperties.class)
public class DruidConfig {

    /**
     * 主数据源（默认 MySQL）
     */
    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.druid")
    public DataSource masterDataSource() {
        return DruidDataSourceBuilder.create().build();
    }

    /**
     * Druid 连接池监控页面访问的用户名密码配置
     * 配置方式：在 application.yml 中设置 spring.datasource.druid.stat-view-servlet.*
     */
}
