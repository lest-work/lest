package com.lest.gateway.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Swagger UI 放行配置
 *
 * <p>允许前端通过 Gateway 访问 Swagger UI 页面。
 * 完整的多服务聚合功能（通过 Nacos 服务发现动态注册各微服务 api-docs）
 * 需要在 Gateway 的 application.yml 中配置路由将各服务的 /v3/api-docs 转发到对应服务。
 * </p>
 *
 * @author yshan2028
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

    /**
     * Swagger UI 和 OpenAPI 文档相关路径白名单
     */
    private static final List<String> SWAGGER_PATHS = List.of(
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/doc.html",
            "/webjars/**",
            "/favicon.ico"
    );

    @Bean
    public WebFilter swaggerWhiteListFilter() {
        return (ServerWebExchange exchange, WebFilterChain chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();

            for (String swaggerPath : SWAGGER_PATHS) {
                if (path.startsWith(swaggerPath.replace("/**", ""))) {
                    // 不走 JWT 认证，直接透传到下游服务
                    return chain.filter(exchange);
                }
            }
            return chain.filter(exchange);
        };
    }
}
