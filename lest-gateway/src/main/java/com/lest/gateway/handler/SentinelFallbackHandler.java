package com.lest.gateway.handler;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * Sentinel 限流异常处理
 */
@Slf4j
public class SentinelFallbackHandler implements WebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        if (exchange.getResponse().isCommitted()) {
            return Mono.error(ex);
        }
        if (!BlockException.isBlockException(ex)) {
            return Mono.error(ex);
        }
        return handleBlockedRequest(exchange, ex)
                .flatMap(response -> writeResponse(exchange.getResponse(), exchange));
    }

    private Mono<Void> writeResponse(ServerHttpResponse response, ServerWebExchange exchange) {
        response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");
        String json = "{\"code\":429,\"msg\":\"请求超过最大数，请稍候再试\"}";
        DataBuffer buffer = response.bufferFactory().wrap(json.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    private reactor.core.publisher.Mono<org.springframework.web.reactive.function.server.ServerResponse> handleBlockedRequest(
            ServerWebExchange exchange, Throwable throwable) {
        return GatewayCallbackManager.getBlockHandler()
                .handleRequest(exchange, throwable);
    }
}
