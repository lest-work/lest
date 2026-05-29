package com.lest.modules.auth.controller;

import com.lest.common.core.Result;
import com.lest.modules.auth.entity.vo.MenuVO;
import com.lest.modules.auth.service.MenuService;
import com.lest.modules.auth.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色菜单权限控制器
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@RestController
@RequestMapping("/auth/role-menu")
@RequiredArgsConstructor
public class RoleMenuController {

    private final RoleService roleService;
    private final MenuService menuService;

    /**
     * 获取角色已分配的菜单ID列表
     */
    @GetMapping("/{roleId}")
    public Result<List<Long>> getMenuIds(@PathVariable Long roleId) {
        return Result.ok(roleService.getMenuIds(roleId));
    }

    /**
     * 更新角色菜单权限
     */
    @PutMapping("/{roleId}")
    public Result<Void> updateMenus(@PathVariable Long roleId, @RequestBody Long[] menuIds) {
        roleService.assignMenus(roleId, menuIds);
        return Result.ok();
    }

    /**
     * 获取角色已分配的菜单树（用于权限分配界面）
     */
    @GetMapping("/{roleId}/tree")
    public Result<List<MenuVO>> getMenuTree(@PathVariable Long roleId) {
        List<Long> checkedIds = roleService.getMenuIds(roleId);
        // 获取完整菜单树后标记选中状态
        List<MenuVO> allMenus = menuService.getTree();
        markChecked(allMenus, checkedIds);
        return Result.ok(allMenus);
    }

    /** 递归标记选中状态 */
    private void markChecked(List<MenuVO> menus, List<Long> checkedIds) {
        for (MenuVO menu : menus) {
            if (checkedIds.contains(menu.getId())) {
                menu.setChecked(true);
            }
            if (menu.getChildren() != null && !menu.getChildren().isEmpty()) {
                markChecked(menu.getChildren(), checkedIds);
            }
        }
    }
}
