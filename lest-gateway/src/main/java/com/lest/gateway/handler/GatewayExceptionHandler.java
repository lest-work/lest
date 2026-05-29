package com.lest.gateway.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

/**
 * 网关统一异常处理
 */
@Slf4j
@Order(-1)
public class GatewayExceptionHandler implements WebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        String msg;
        if (ex instanceof NotFoundException) {
            msg = "服务未找到";
        } else if (ex instanceof ResponseStatusException) {
            msg = ((ResponseStatusException) ex).getReason();
        } else {
            msg = "内部服务器错误";
        }

        log.error("[网关异常] path={}, error={}", exchange.getRequest().getPath(), ex.getMessage());
        return webFluxResponseWriter(response, msg, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Mono<Void> webFluxResponseWriter(ServerHttpResponse response, String msg, HttpStatus status) {
        response.setStatusCode(status);
        response.getHeaders().add(org.springframework.http.HttpHeaders.CONTENT_TYPE, "application/json");
        String json = "{\"code\":" + status.value() + ",\"msg\":\"" + msg + "\"}";
        byte[] bytes = json.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        org.springframework.core.io.buffer.DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(reactor.core.publisher.Mono.just(buffer));
    }
}
