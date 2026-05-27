package com.lest.modules.auth.service;

import com.lest.modules.auth.entity.dto.LoginDTO;
import com.lest.modules.auth.entity.dto.PasswordChangeDTO;
import com.lest.modules.auth.entity.dto.TokenRefreshDTO;
import com.lest.modules.auth.entity.vo.CaptchaVO;
import com.lest.modules.auth.entity.vo.CurrentUserVO;
import com.lest.modules.auth.entity.vo.LoginVO;

/**
 * 认证服务接口
 *
 * @author yshan2028
 * @since 2026-05-26
 */
public interface AuthService {

    CaptchaVO getCaptcha();
    LoginVO login(LoginDTO loginDTO);
    LoginVO refreshToken(TokenRefreshDTO dto);
    CurrentUserVO getCurrentUser();
    void changePassword(PasswordChangeDTO dto);
    void logout();
}
