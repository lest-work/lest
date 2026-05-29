package com.lest.modules.auth.controller;

import com.lest.common.core.Result;
import com.lest.common.core.constant.CacheConstants;
import com.lest.common.core.constant.SecurityConstants;
import com.lest.common.core.exception.ServiceException;
import com.lest.common.core.utils.ServletUtils;
import com.lest.modules.auth.entity.dto.LoginDTO;
import com.lest.modules.auth.entity.dto.PasswordChangeDTO;
import com.lest.modules.auth.entity.dto.TokenRefreshDTO;
import com.lest.modules.auth.entity.vo.CurrentUserVO;
import com.lest.modules.auth.entity.vo.LoginVO;
import com.lest.modules.auth.entity.vo.CaptchaVO;
import com.lest.modules.auth.service.AuthService;
import com.lest.system.api.RemoteLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RemoteLogService remoteLogService;

    @GetMapping("/captcha")
    public Result<CaptchaVO> getCaptcha() {
        return Result.ok(authService.getCaptcha());
    }

    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO, HttpServletRequest request) {
        log.info("用户登录请求: username={}", loginDTO.getUsername());
        try {
            return Result.ok(authService.login(loginDTO));
        } catch (ServiceException e) {
            recordLoginLog(loginDTO.getUsername(), 0, e.getMessage(), request);
            return Result.fail(e.getCode(), e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public Result<LoginVO> refresh(@Valid @RequestBody TokenRefreshDTO dto) {
        return Result.ok(authService.refreshToken(dto));
    }

    @GetMapping("/current-user")
    public Result<CurrentUserVO> getCurrentUser() {
        return Result.ok(authService.getCurrentUser());
    }

    @PutMapping("/password")
    public Result<Void> changePassword(@Valid @RequestBody PasswordChangeDTO dto) {
        authService.changePassword(dto);
        return Result.ok();
    }

    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        authService.logout(request);
        return Result.ok();
    }

    private void recordLoginLog(String username, Integer status, String msg, HttpServletRequest request) {
        try {
            String ip = ServletUtils.getClientIP(request);
            remoteLogService.saveLoginLog(username, status, msg, ip, SecurityConstants.INNER);
        } catch (Exception e) {
            log.warn("记录登录日志失败: {}", e.getMessage());
        }
    }
}
