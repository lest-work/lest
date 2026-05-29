package com.lest.gateway.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.lest.common.core.utils.ServletUtils;
import com.lest.common.core.utils.StringUtils;
import com.lest.gateway.config.properties.CaptchaProperties;
import com.lest.gateway.service.ValidateCodeService;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * 验证码过滤器
 *
 * @author yshan2028
 */
@Component
public class ValidateCodeFilter extends AbstractGatewayFilterFactory<Object>
{
    private final static String[] VALIDATE_URL = new String[] { "/auth/login", "/auth/register" };

    @Autowired
    private ValidateCodeService validateCodeService;

    @Autowired
    private CaptchaProperties captchaProperties;

    private static final String CODE = "code";

    private static final String UUID = "uuid";

    @Override
    public GatewayFilter apply(Object config)
    {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // 非登录/注册请求或验证码关闭，不处理
            if (!StringUtils.equalsAnyIgnoreCase(request.getURI().getPath(), VALIDATE_URL) || !captchaProperties.getEnabled())
            {
                return chain.filter(exchange);
            }

            return DataBufferUtils.join(request.getBody())
                .flatMap(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    String bodyStr = new String(bytes, StandardCharsets.UTF_8);
                    try
                    {
                        JSONObject obj = JSON.parseObject(bodyStr);
                        validateCodeService.checkCaptcha(obj.getString(CODE), obj.getString(UUID));
                    }
                    catch (Exception e)
                    {
                        return ServletUtils.webFluxResponseWriter(exchange.getResponse(), e.getMessage());
                    }
                    // 重新包装请求体（因为已经被消费了）
                    var factory = exchange.getResponse().bufferFactory();
                    var newBuffer = factory.wrap(bytes);
                    var decorator = new org.springframework.http.server.reactive.ServerHttpRequestDecorator(request) {
                        @Override
                        public reactor.core.publisher.Flux<org.springframework.core.io.buffer.DataBuffer> getBody() {
                            return reactor.core.publisher.Flux.just(newBuffer);
                        }
                    };
                    return chain.filter(exchange.mutate().request(decorator).build());
                })
                .switchIfEmpty(Mono.defer(() -> chain.filter(exchange)));
        };
    }
}
