package com.lest.gateway.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Swagger API 文档聚合配置
 *
 * <p>原理：监听 Nacos 服务实例变化事件，动态构建各微服务的 /v3/api-docs 路由。
 * 前端访问 Gateway 的 /swagger-ui.html 即可看到所有服务的接口文档。
 * </p>
 *
 * @author yshan2028
 */
@Slf4j
@Configuration
public class SwaggerAggregatorConfig {

    /**
     * 需要聚合的微服务名称列表（排除 gateway/auth/file/monitor）
     */
    private static final Set<String> EXCLUDED_SERVICES = Set.of(
            "lest-gateway",
            "lest-auth",
            "lest-file"
    );

    private final DiscoveryClient discoveryClient;

    /**
     * 缓存各微服务的 api-docs 路径
     */
    private final Map<String, String> serviceApiDocs = new ConcurrentHashMap<>();

    public SwaggerAggregatorConfig(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    /**
     * 获取所有已注册微服务的 api-docs 路径
     */
    public List<SwaggerResource> getSwaggerResources() {
        List<String> services = discoveryClient.getServices();
        if (services == null || services.isEmpty()) {
            return Collections.emptyList();
        }

        return services.stream()
                .filter(name -> !EXCLUDED_SERVICES.contains(name))
                .map(name -> {
                    SwaggerResource resource = new SwaggerResource();
                    resource.setName(name);
                    // 网关通过 lb:// 路由到目标服务的 /v3/api-docs
                    resource.setUrl("lb://" + name + "/v3/api-docs");
                    resource.setLocation("lb://" + name + "/v3/api-docs");
                    resource.setSwaggerVersion("3.0");
                    return resource;
                })
                .collect(Collectors.toList());
    }

    @Data
    public static class SwaggerResource {
        private String name;
        private String url;
        private String location;
        private String swaggerVersion;
    }
}
