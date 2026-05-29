package com.lest.system.api;

import com.lest.common.core.constant.SecurityConstants;
import com.lest.common.core.web.domain.R;
import com.lest.system.api.domain.SysRole;
import com.lest.system.api.factory.RemoteRoleFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色服务 Feign 客户端
 *
 * @author yshan2028
 */
@FeignClient(contextId = "remoteRoleService", value = "lest-auth", fallbackFactory = RemoteRoleFallbackFactory.class)
public interface RemoteRoleService {

    /**
     * 获取角色信息
     */
    @GetMapping("/auth/role/{id}")
    R<SysRole> getById(
            @PathVariable("id") Long id,
            @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 获取所有角色
     */
    @GetMapping("/auth/role")
    R<List<SysRole>> listAll(
            @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 获取角色已分配的菜单ID列表
     */
    @GetMapping("/auth/role-menu/{roleId}")
    R<List<Long>> getMenuIds(
            @PathVariable("roleId") Long roleId,
            @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 更新角色菜单权限
     */
    @PutMapping("/auth/role-menu/{roleId}")
    R<Boolean> updateMenus(
            @PathVariable("roleId") Long roleId,
            @RequestBody Long[] menuIds,
            @RequestHeader(SecurityConstants.FROM_SOURCE) String source);
}
