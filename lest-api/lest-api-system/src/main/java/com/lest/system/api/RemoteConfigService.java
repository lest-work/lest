package com.lest.system.api;

import com.lest.common.core.constant.SecurityConstants;
import com.lest.common.core.web.domain.R;
import com.lest.system.api.factory.RemoteConfigFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * 系统配置 Feign 客户端
 *
 * @author yshan2028
 */
@FeignClient(contextId = "remoteConfigService", value = "lest-system", fallbackFactory = RemoteConfigFallbackFactory.class)
public interface RemoteConfigService {

    /**
     * 根据配置键查询配置值
     */
    @GetMapping("/config/configKey/{configKey}")
    R<String> getConfigKey(@PathVariable("configKey") String configKey,
                           @RequestHeader(SecurityConstants.FROM_SOURCE) String source);
}
