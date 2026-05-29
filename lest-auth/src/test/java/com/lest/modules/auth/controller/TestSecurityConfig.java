package com.lest.modules.auth.controller;

import com.lest.modules.auth.security.JwtAuthenticationEntryPoint;
import com.lest.modules.auth.security.JwtAuthenticationFilter;
import com.lest.modules.auth.security.JwtTokenProvider;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import static org.mockito.Mockito.mock;

/**
 * 测试用配置：
 * 1. Mock JWT 相关 bean，让 SecurityConfig 能正常初始化
 * 2. 用同名 bean "securityFilterChain" + allow-bean-definition-overriding=true 覆盖原 filter chain，
 *    放行所有请求，专注于 Controller 逻辑测试
 */
@TestConfiguration
public class TestSecurityConfig {

    @Bean
    public JwtTokenProvider jwtTokenProvider() {
        return mock(JwtTokenProvider.class);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return mock(JwtAuthenticationFilter.class);
    }

    @Bean
    public JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint() {
        return mock(JwtAuthenticationEntryPoint.class);
    }

    /**
     * 覆盖 SecurityConfig 中的 securityFilterChain，放行所有请求。
     * 依赖 spring.main.allow-bean-definition-overriding=true（已在 test/resources/application.yml 中配置）。
     */
    @Bean("securityFilterChain")
    @Primary
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
}
