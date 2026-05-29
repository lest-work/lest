package com.lest.system.api;

import com.lest.common.core.constant.SecurityConstants;
import com.lest.common.core.web.domain.R;
import com.lest.system.api.domain.SysLogininfor;
import com.lest.system.api.factory.RemoteLogFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 日志服务 Feign 客户端
 *
 * @author yshan2028
 */
@FeignClient(contextId = "remoteLogService", value = "lest-system", fallbackFactory = RemoteLogFallbackFactory.class)
public interface RemoteLogService {

    /**
     * 记录登录日志
     */
    @PostMapping("/log/login")
    R<Boolean> saveLoginLog(
            @RequestParam("username") String username,
            @RequestParam("status") Integer status,
            @RequestParam("msg") String msg,
            @RequestParam("ip") String ip,
            @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 记录登录日志（通过 body 传递）
     */
    @PostMapping("/log/login/body")
    R<Boolean> saveLoginLogBody(
            @RequestBody SysLogininfor logininfor,
            @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 记录操作日志
     */
    @PostMapping("/log/operate")
    R<Boolean> saveOperateLog(
            @RequestParam("username") String username,
            @RequestParam("operation") String operation,
            @RequestParam("method") String method,
            @RequestParam("params") String params,
            @RequestParam("result") String result,
            @RequestParam("ip") String ip,
            @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 记录操作日志（通过 body 传递）
     */
    @PostMapping("/log/operate/body")
    R<Boolean> saveOperateLogBody(
            @RequestBody com.lest.system.api.domain.SysOperLog operLog,
            @RequestHeader(SecurityConstants.FROM_SOURCE) String source);
}
