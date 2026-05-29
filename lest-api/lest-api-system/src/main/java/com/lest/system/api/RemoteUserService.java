package com.lest.system.api;

import com.lest.common.core.constant.SecurityConstants;
import com.lest.common.core.web.domain.R;
import com.lest.system.api.domain.SysUser;
import com.lest.system.api.domain.SysUserVO;
import com.lest.system.api.factory.RemoteUserFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户服务 Feign 客户端
 *
 * @author yshan2028
 */
@FeignClient(contextId = "remoteUserService", value = "lest-auth", fallbackFactory = RemoteUserFallbackFactory.class)
public interface RemoteUserService {

    /**
     * 获取用户信息
     */
    @GetMapping("/auth/user/info/{username}")
    R<com.lest.system.api.model.LoginUser> getUserInfo(
            @PathVariable("username") String username,
            @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 注册用户信息
     */
    @PostMapping("/auth/user/register")
    R<Boolean> registerUserInfo(
            @RequestBody SysUser sysUser,
            @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 记录用户登录
     */
    @PutMapping("/auth/user/recordlogin")
    R<Boolean> recordUserLogin(
            @RequestBody SysUser sysUser,
            @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 根据ID查询用户
     */
    @GetMapping("/auth/user/{id}")
    R<SysUserVO> getById(
            @PathVariable("id") Long id,
            @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 获取所有用户
     */
    @GetMapping("/auth/user")
    R<List<SysUserVO>> listAll(
            @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 获取用户角色ID列表
     */
    @GetMapping("/auth/user/{id}/roles")
    R<List<Long>> getRoleIds(
            @PathVariable("id") Long id,
            @RequestHeader(SecurityConstants.FROM_SOURCE) String source);
}
