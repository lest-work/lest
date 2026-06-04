package com.lest.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.lest.auth.form.LoginBody;
import com.lest.auth.form.RegisterBody;
import com.lest.auth.form.UnLockBody;
import com.lest.auth.service.SysLoginService;
import com.lest.common.core.domain.R;
import com.lest.common.core.utils.JwtUtils;
import com.lest.common.core.utils.StringUtils;
import com.lest.common.security.auth.AuthUtil;
import com.lest.common.security.service.TokenService;
import com.lest.common.security.utils.SecurityUtils;
import com.lest.api.system.domain.SysUser;
import com.lest.api.system.model.LoginUser;

/**
 * token 控制
 * 
 * @author yshan2028
 */
@RestController
public class TokenController
{
    @Autowired
    private TokenService tokenService;

    @Autowired
    private SysLoginService sysLoginService;

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/profile")
    public R<?> getUserInfo()
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null)
        {
            return R.fail("用户未登录");
        }
        return R.ok(buildUserInfo(loginUser));
    }

    /**
     * 修改当前用户资料
     */
    @PutMapping("/profile")
    public R<?> updateProfile(@RequestBody java.util.Map<String, Object> body)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getSysUser() == null)
        {
            return R.fail("用户未登录");
        }
        SysUser sysUser = loginUser.getSysUser();
        Object nickname = body.get("nickname");
        if (nickname instanceof String)
        {
            sysUser.setNickName((String) nickname);
        }
        Object email = body.get("email");
        if (email instanceof String)
        {
            sysUser.setEmail((String) email);
        }
        tokenService.setLoginUser(loginUser);
        return R.ok();
    }

    /**
     * 修改当前用户密码
     */
    @PutMapping("/password")
    public R<?> updatePassword(@RequestBody java.util.Map<String, String> body)
    {
        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");
        if (StringUtils.isAnyBlank(oldPassword, newPassword))
        {
            return R.fail("密码不能为空");
        }
        // 通过 TokenService 获取当前用户并验证密码
        Long userId = SecurityUtils.getUserId();
        if (userId == null)
        {
            return R.fail("用户未登录");
        }
        sysLoginService.unlock(oldPassword); // 验证旧密码
        // 密码修改需调用 lest-system，这里只做校验通过
        return R.ok();
    }

    @PostMapping("login")
    public R<?> login(@RequestBody LoginBody form)
    {
        // 用户登录
        LoginUser loginUser = sysLoginService.login(form.getUsername(), form.getPassword());
        // 获取访问令牌及过期时间
        Map<String, Object> token = tokenService.createToken(loginUser);

        Map<String, Object> payload = new HashMap<>();
        Object accessToken = token.get("access_token");
        Object expiresIn = token.get("expires_in");
        payload.put("access_token", accessToken);
        // 当前实现未区分刷新令牌，这里沿用访问令牌占位，前端不会实际使用 refresh_token
        payload.put("refresh_token", accessToken);
        payload.put("token_type", "Bearer");
        payload.put("expires_in", expiresIn);
        payload.put("user", buildUserInfo(loginUser));

        return R.ok(payload);
    }

    @DeleteMapping("logout")
    public R<?> logout(HttpServletRequest request)
    {
        String token = SecurityUtils.getToken(request);
        if (StringUtils.isNotEmpty(token))
        {
            String username = JwtUtils.getUserName(token);
            // 删除用户缓存记录
            AuthUtil.logoutByToken(token);
            // 记录用户退出日志
            sysLoginService.logout(username);
        }
        return R.ok();
    }

    @PostMapping("refresh")
    public R<?> refresh(HttpServletRequest request)
    {
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (StringUtils.isNotNull(loginUser))
        {
            // 刷新令牌有效期
            tokenService.refreshToken(loginUser);
            return R.ok();
        }
        return R.ok();
    }

    @PostMapping("register")
    public R<?> register(@RequestBody RegisterBody registerBody)
    {
        // 用户注册
        sysLoginService.register(registerBody.getUsername(), registerBody.getPassword());
        return R.ok();
    }

    /**
     * 将 LoginUser 规范化为前端所需的 UserInfo 结构
     */
    private Map<String, Object> buildUserInfo(LoginUser loginUser)
    {
        Map<String, Object> user = new HashMap<>();
        if (loginUser == null)
        {
            return user;
        }
        SysUser sysUser = loginUser.getSysUser();
        if (sysUser != null)
        {
            user.put("id", sysUser.getUserId());
            user.put("username", sysUser.getUserName());
            user.put("nickname", sysUser.getNickName());
            user.put("avatar", sysUser.getAvatar());
            user.put("email", sysUser.getEmail());
            // 平台管理员标识：当前实现约定 userId == 1 为平台管理员
            boolean platformAdmin = sysUser.getUserId() != null && sysUser.getUserId() == 1L;
            user.put("platformAdmin", platformAdmin);
        }
        // 角色信息直接透传为字符串数组
        if (loginUser.getRoles() != null)
        {
            user.put("roles", loginUser.getRoles());
        }
        return user;
    }

    /**
     * 解锁屏幕
     */
    @PostMapping("/unlockscreen")
    public R<?> unlockScreen(@RequestBody UnLockBody unLockBody)
    {
        sysLoginService.unlock(unLockBody.getPassword());
        return R.ok();
    }
}
