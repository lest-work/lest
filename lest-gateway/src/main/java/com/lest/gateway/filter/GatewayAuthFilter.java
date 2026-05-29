package com.lest.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lest.common.core.constant.SecurityConstants;
import com.lest.common.core.constant.TokenConstants;
import com.lest.common.security.util.LoginUser;
import com.lest.gateway.config.properties.GatewayAuthProperties;
import com.lest.gateway.config.properties.IgnoreWhiteProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 网关认证过滤器 — 统一鉴权
 *
 * @author yshan2028
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GatewayAuthFilter implements GlobalFilter, Ordered {

    private static final String LOGIN_TOKEN_KEY_PREFIX = com.lest.common.core.constant.CacheConstants.LOGIN_TOKEN_KEY;

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    private final GatewayAuthProperties authProperties;

    private ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public void setReactiveRedisTemplate(ReactiveRedisTemplate<String, Object> reactiveRedisTemplate) {
        this.reactiveRedisTemplate = reactiveRedisTemplate;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String url = request.getURI().getPath();

        if (isWhiteListed(url)) {
            return chain.filter(exchange);
        }

        if (!authProperties.isEnabled()) {
            return chain.filter(exchange);
        }

        String token = getToken(request);
        if (!org.springframework.util.StringUtils.hasText(token)) {
            return unauthorizedResponse(exchange, "令牌不能为空");
        }

        if (!com.lest.common.security.util.JwtUtils.isValid(token)) {
            return unauthorizedResponse(exchange, "令牌已过期或验证不正确！");
        }

        String userkey = com.lest.common.security.util.JwtUtils.getUserKey(token);
        if (userkey == null) {
            return unauthorizedResponse(exchange, "令牌验证失败");
        }

        Mono<LoginUser> loginUserMono = reactiveRedisTemplate.opsForValue().get(LOGIN_TOKEN_KEY_PREFIX + userkey)
                .cast(LoginUser.class);

        return loginUserMono.flatMap(loginUser -> {
            if (loginUser == null) {
                return unauthorizedResponse(exchange, "登录状态已过期");
            }

            Long userId = com.lest.common.security.util.JwtUtils.getUserId(token);
            String username = com.lest.common.security.util.JwtUtils.getUsername(token);
            if (userId == null || username == null) {
                return unauthorizedResponse(exchange, "令牌验证失败");
            }

            final String finalUserkey = userkey;
            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header(SecurityConstants.USER_KEY, finalUserkey)
                    .header(SecurityConstants.DETAILS_USER_ID, String.valueOf(userId))
                    .header(SecurityConstants.DETAILS_USERNAME, username)
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        }).switchIfEmpty(Mono.defer(() -> unauthorizedResponse(exchange, "登录状态已过期")));
    }

    /**
     * 获取请求token
     */
    private String getToken(ServerHttpRequest request) {
        String token = request.getHeaders().getFirst(SecurityConstants.AUTHORIZATION_HEADER);
        if (org.springframework.util.StringUtils.hasText(token) && token.startsWith(TokenConstants.PREFIX)) {
            token = token.replaceFirst(TokenConstants.PREFIX, "");
        }
        return token;
    }

    /**
     * 检查是否在白名单中
     */
    private boolean isWhiteListed(String url) {
        IgnoreWhiteProperties white = authProperties.getIgnoreWhite();
        if (white == null || white.getWhitelist() == null) {
            return false;
        }
        List<String> whites = white.getWhitelist();
        for (String pattern : whites) {
            if (PATH_MATCHER.match(pattern, url)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 返回错误响应
     */
    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String msg) {
        log.error("[鉴权异常处理]请求路径:{},错误信息:{}", exchange.getRequest().getPath(), msg);
        return webFluxResponseWriter(exchange.getResponse(), msg, HttpStatus.UNAUTHORIZED);
    }

    /**
     * 写入响应
     */
    private Mono<Void> webFluxResponseWriter(ServerHttpResponse response, String msg, HttpStatus status) {
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> body = new HashMap<>(4);
        body.put("code", String.valueOf(status.value()));
        body.put("msg", msg);
        String json;
        try {
            json = objectMapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            json = "{\"code\":\"" + status.value() + "\",\"msg\":\"" + msg + "\"}";
        }
        DataBuffer buffer = response.bufferFactory().wrap(json.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -200;
    }
}
