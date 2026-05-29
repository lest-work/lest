package com.lest.gateway.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 网关认证配置
 *
 * @author yshan2028
 */
@Data
@ConfigurationProperties(prefix = "gateway-auth")
public class GatewayAuthProperties {

    /**
     * 是否开启网关认证（默认开启）
     */
    private boolean enabled = true;

    /**
     * 白名单配置
     */
    private IgnoreWhiteProperties ignoreWhite = new IgnoreWhiteProperties();
}
