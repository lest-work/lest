package com.lest.system.api;

import com.lest.common.core.constant.SecurityConstants;
import com.lest.common.core.web.domain.R;
import com.lest.system.api.domain.SysMenu;
import com.lest.system.api.domain.vo.SysMenuVO;
import com.lest.system.api.factory.RemoteMenuFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单服务 Feign 客户端
 *
 * @author yshan2028
 */
@FeignClient(contextId = "remoteMenuService", value = "lest-auth", fallbackFactory = RemoteMenuFallbackFactory.class)
public interface RemoteMenuService {

    /**
     * 获取菜单树
     */
    @GetMapping("/auth/menu/tree")
    R<List<SysMenuVO>> getTree(
            @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 获取所有菜单
     */
    @GetMapping("/auth/menu")
    R<List<SysMenuVO>> listAll(
            @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 根据ID查询菜单
     */
    @GetMapping("/auth/menu/{id}")
    R<SysMenuVO> getById(
            @PathVariable("id") Long id,
            @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 获取路由列表（用于前端动态路由）
     */
    @GetMapping("/auth/menu/routes")
    R<List<SysMenu>> getRoutes(
            @RequestHeader(SecurityConstants.FROM_SOURCE) String source);
}
