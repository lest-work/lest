package com.lest.modules.system.entity.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

/**
 * 操作日志视图对象，用于API返回
 *
 * @author yshan2028
 * @since 2026-05-26
 */
public record LogVO(
    Long id,
    Long userId,
    String username,
    String nickname,
    String module,
    String description,
    String url,
    String requestMethod,
    String method,
    String params,
    String result,
    String error,
    Integer spendTime,
    String os,
    String device,
    String browser,
    String ipAddress,
    Integer status,
    LocalDateTime createTime
) {

    /** 前端别名: IP地址 */
    @JsonProperty("ip")
    public String getIpAlias() { return this.ipAddress; }
}
