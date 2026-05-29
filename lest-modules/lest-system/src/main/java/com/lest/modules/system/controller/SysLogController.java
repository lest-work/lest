package com.lest.modules.system.controller;

import com.lest.common.core.web.domain.AjaxResult;
import com.lest.common.security.annotation.InnerAuth;
import com.lest.modules.system.entity.domain.SysLoginLog;
import com.lest.modules.system.service.LoginLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 日志服务（Feign 接口）
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/log")
public class SysLogController {

    private final LoginLogService loginLogService;

    @InnerAuth
    @PostMapping("/login")
    public AjaxResult saveLoginLog(
            @RequestParam("username") String username,
            @RequestParam("status") Integer status,
            @RequestParam("msg") String msg,
            @RequestParam("ip") String ip) {
        SysLoginLog log = new SysLoginLog();
        log.setUsername(username);
        log.setStatus(status);
        log.setMsg(msg);
        log.setIpAddress(ip);
        loginLogService.save(log);
        return AjaxResult.success();
    }
}
