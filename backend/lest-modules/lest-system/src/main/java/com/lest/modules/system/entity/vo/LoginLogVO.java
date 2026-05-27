package com.lest.modules.system.entity.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

/**
 * 登录日志视图对象，用于API返回
 *
 * @author yshan2028
 * @since 2026-05-26
 */
public record LoginLogVO(
    Long id,
    Long userId,
    String username,
    String nickname,
    Integer loginType,
    String ipAddress,
    String userAgent,
    String os,
    String browser,
    String device,
    Integer status,
    String msg,
    LocalDateTime createTime
) {

    /** 前端别名: IP地址 */
    @JsonProperty("ip")
    public String getIpAlias() { return this.ipAddress; }

    /** 前端别名: 操作时间 */
    @JsonProperty("createTime")
    public LocalDateTime getCreateTimeAlias() { return this.createTime; }
}
