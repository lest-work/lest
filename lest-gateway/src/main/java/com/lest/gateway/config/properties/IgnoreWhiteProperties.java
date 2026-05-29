package com.lest.gateway.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 放行白名单配置
 *
 * @author yshan2028
 */
@Data
@ConfigurationProperties(prefix = "security.ignore")
public class IgnoreWhiteProperties {

    /**
     * 放行白名单配置
     */
    private List<String> whites = new ArrayList<>();

    /**
     * 获取白名单列表
     */
    public List<String> getWhitelist() {
        return whites;
    }

    /**
     * 设置白名单列表
     */
    public void setWhitelist(List<String> whitelist) {
        this.whites = whitelist;
    }
}
