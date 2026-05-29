package com.lest.modules.system.controller;

import com.lest.common.core.PageResult;
import com.lest.common.core.Result;
import com.lest.common.core.web.controller.BaseController;
import com.lest.common.core.web.page.TableDataInfo;
import com.lest.common.log.annotation.Log;
import com.lest.common.log.enums.BusinessType;
import com.lest.modules.system.entity.dto.MenuDTO;
import com.lest.modules.system.entity.vo.MenuVO;
import com.lest.modules.system.service.MenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.lest.common.security.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单管理 Controller
 *
 * @author yshan2028
 * @since 2026-05-28
 */
@Slf4j
@RestController
@RequestMapping("/system/menu")
@RequiredArgsConstructor
public class SysMenuController extends BaseController {

    private final MenuService menuService;

    /**
     * 分页查询菜单列表
     */
    @RequiresPermissions("system:menu:list")
    @GetMapping("/list")
    public TableDataInfo list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String menuName,
            @RequestParam(required = false) String path,
            @RequestParam(required = false) String permission) {
        startPage();
        List<MenuVO> list = menuService.selectMenuList(menuName, path, permission);
        return getDataTable(list);
    }

    /**
     * 获取菜单树
     */
    @RequiresPermissions("system:menu:query")
    @GetMapping("/tree")
    public Result<List<MenuVO>> tree() {
        return Result.ok(menuService.selectMenuTree());
    }

    /**
     * 获取菜单详情
     */
    @RequiresPermissions("system:menu:query")
    @GetMapping("/{menuId}")
    public Result<MenuVO> getById(@PathVariable("menuId") Long menuId) {
        return Result.ok(menuService.getById(menuId));
    }

    /**
     * 新增菜单
     */
    @RequiresPermissions("system:menu:add")
    @Log(title = "菜单管理", businessType = BusinessType.INSERT)
    @PostMapping
    public Result<Long> create(@Valid @RequestBody MenuDTO dto) {
        return Result.ok(menuService.createMenu(dto));
    }

    /**
     * 修改菜单
     */
    @RequiresPermissions("system:menu:edit")
    @Log(title = "菜单管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result<Void> update(@Valid @RequestBody MenuDTO dto) {
        menuService.updateMenu(dto);
        return Result.ok();
    }

    /**
     * 删除菜单
     */
    @RequiresPermissions("system:menu:remove")
    @Log(title = "菜单管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{menuId}")
    public Result<Void> delete(@PathVariable Long menuId) {
        menuService.deleteMenu(menuId);
        return Result.ok();
    }

    /**
     * 获取角色菜单树
     */
    @RequiresPermissions("system:menu:query")
    @GetMapping("/roleMenuTreeselect/{roleId}")
    public Result<List<MenuVO>> roleMenuTreeselect(@PathVariable("roleId") Long roleId) {
        List<MenuVO> allMenus = menuService.selectMenuTree();
        List<Long> checkedMenuIds = menuService.getMenuIdsByRoleId(roleId);
        return Result.ok(menuService.buildRoleMenuTree(allMenus, checkedMenuIds));
    }

    /**
     * 获取菜单权限（用户菜单树）
     */
    @RequiresPermissions("system:menu:query")
    @GetMapping("/menuTreeselect/{userId}")
    public Result<List<MenuVO>> menuTreeselect(@PathVariable("userId") Long userId) {
        List<MenuVO> allMenus = menuService.selectMenuTree();
        List<Long> checkedMenuIds = menuService.getMenuIdsByUserId(userId);
        return Result.ok(menuService.buildRoleMenuTree(allMenus, checkedMenuIds));
    }
}
