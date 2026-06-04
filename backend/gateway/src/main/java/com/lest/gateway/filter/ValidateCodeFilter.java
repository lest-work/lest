package com.lest.gateway.filter;

import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.lest.common.core.utils.ServletUtils;
import com.lest.common.core.utils.StringUtils;
import com.lest.gateway.config.properties.CaptchaProperties;
import com.lest.gateway.service.ValidateCodeService;
import reactor.core.publisher.Flux;


/**
 * 验证码过滤器
 *
 * @author ruoyi
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
                    try
                    {
                        dataBuffer.readPosition(0);
                        CharBuffer charBuffer = StandardCharsets.UTF_8.decode(dataBuffer.toByteBuffer());
                        String rspStr = charBuffer.toString();
                        DataBufferUtils.release(dataBuffer);
                        
                        JSONObject obj = JSON.parseObject(rspStr);
                        validateCodeService.checkCaptcha(obj.getString(CODE), obj.getString(UUID));
                        
                        // 重新包装请求体，以便后续过滤器可以读取
                        ServerHttpRequest newRequest = new ServerHttpRequestDecorator(request)
                        {
                            @Override
                            public Flux<DataBuffer> getBody()
                            {
                                return Flux.just(dataBuffer);
                            }
                        };
                        
                        return chain.filter(exchange.mutate().request(newRequest).build());
                    }
                    catch (Exception e)
                    {
                        DataBufferUtils.release(dataBuffer);
                        return ServletUtils.webFluxResponseWriter(exchange.getResponse(), e.getMessage());
                    }
                });
        };
    }
}
