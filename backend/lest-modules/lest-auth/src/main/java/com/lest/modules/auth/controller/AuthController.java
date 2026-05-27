package com.lest.modules.auth.controller;

import com.lest.common.base.Result;
import com.lest.modules.auth.entity.dto.LoginDTO;
import com.lest.modules.auth.entity.dto.PasswordChangeDTO;
import com.lest.modules.auth.entity.dto.TokenRefreshDTO;
import com.lest.modules.auth.entity.vo.CurrentUserVO;
import com.lest.modules.auth.entity.vo.LoginVO;
import com.lest.modules.auth.entity.vo.CaptchaVO;
import com.lest.modules.auth.service.AuthService;
import jakarta.validation.Valid;
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
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 获取验证码
     *
     * @return 验证码VO
     */
    @GetMapping("/captcha")
    public Result<CaptchaVO> getCaptcha() {
        return Result.ok(authService.getCaptcha());
    }

    /**
     * 用户登录
     *
     * @param loginDTO 登录信息
     * @return 登录结果VO
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        log.info("用户登录请求: username={}", loginDTO.getUsername());
        return Result.ok(authService.login(loginDTO));
    }

    /**
     * 刷新Token
     *
     * @param dto 刷新令牌DTO
     * @return 新的Token对
     */
    @PostMapping("/refresh")
    public Result<LoginVO> refresh(@Valid @RequestBody TokenRefreshDTO dto) {
        return Result.ok(authService.refreshToken(dto));
    }

    /**
     * 获取当前用户信息
     *
     * @return 当前用户信息VO
     */
    @GetMapping("/current-user")
    public Result<CurrentUserVO> getCurrentUser() {
        return Result.ok(authService.getCurrentUser());
    }

    /**
     * 修改密码
     *
     * @param dto 密码修改DTO
     * @return 操作结果
     */
    @PutMapping("/password")
    public Result<Void> changePassword(@Valid @RequestBody PasswordChangeDTO dto) {
        authService.changePassword(dto);
        return Result.ok();
    }

    /**
     * 登出
     *
     * @return 操作结果
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.ok();
    }
}
