package com.lest.system.api.factory;

import com.lest.common.core.web.domain.R;
import com.lest.system.api.RemoteConfigService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 配置服务降级工厂
 *
 * @author yshan2028
 */
@Slf4j
@Component
public class RemoteConfigFallbackFactory implements FallbackFactory<RemoteConfigService> {

    @Override
    public RemoteConfigService create(Throwable cause) {
        log.error("RemoteConfigService 调用失败: {}", cause.getMessage());
        return (configKey, source) -> R.fail("查询配置失败: " + cause.getMessage());
    }
}
