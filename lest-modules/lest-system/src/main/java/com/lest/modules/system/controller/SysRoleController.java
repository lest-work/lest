package com.lest.modules.system.controller;

import com.lest.common.core.PageResult;
import com.lest.common.core.Result;
import com.lest.common.core.web.controller.BaseController;
import com.lest.common.core.web.page.TableDataInfo;
import com.lest.common.log.annotation.Log;
import com.lest.common.log.enums.BusinessType;
import com.lest.modules.system.entity.dto.RoleDTO;
import com.lest.modules.system.entity.vo.RoleVO;
import com.lest.modules.system.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.lest.common.security.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理 Controller
 *
 * @author yshan2028
 * @since 2026-05-28
 */
@Slf4j
@RestController
@RequestMapping("/system/role")
@RequiredArgsConstructor
public class SysRoleController extends BaseController {

    private final RoleService roleService;

    /**
     * 分页查询角色列表
     */
    @RequiresPermissions("system:role:list")
    @GetMapping("/list")
    public TableDataInfo list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String roleCode,
            @RequestParam(required = false) String roleName,
            @RequestParam(required = false) Integer status) {
        startPage();
        List<RoleVO> list = roleService.selectRoleList(roleCode, roleName, status);
        return getDataTable(list);
    }

    /**
     * 获取所有角色（用于下拉选择）
     */
    @RequiresPermissions("system:role:list")
    @GetMapping("/all")
    public Result<List<RoleVO>> listAll() {
        return Result.ok(roleService.selectAllRoles());
    }

    /**
     * 获取角色详情
     */
    @RequiresPermissions("system:role:query")
    @GetMapping("/{roleId}")
    public Result<RoleVO> getById(@PathVariable("roleId") Long roleId) {
        return Result.ok(roleService.getById(roleId));
    }

    /**
     * 新增角色
     */
    @RequiresPermissions("system:role:add")
    @Log(title = "角色管理", businessType = BusinessType.INSERT)
    @PostMapping
    public Result<Long> create(@Valid @RequestBody RoleDTO dto) {
        return Result.ok(roleService.createRole(dto));
    }

    /**
     * 修改角色
     */
    @RequiresPermissions("system:role:edit")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result<Void> update(@Valid @RequestBody RoleDTO dto) {
        roleService.updateRole(dto);
        return Result.ok();
    }

    /**
     * 删除角色
     */
    @RequiresPermissions("system:role:remove")
    @Log(title = "角色管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{roleId}")
    public Result<Void> delete(@PathVariable Long roleId) {
        roleService.deleteRole(roleId);
        return Result.ok();
    }

    /**
     * 修改角色状态
     */
    @RequiresPermissions("system:role:edit")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public Result<Void> changeStatus(@RequestParam Long roleId, @RequestParam Integer status) {
        roleService.updateRoleStatus(roleId, status);
        return Result.ok();
    }

    /**
     * 获取角色菜单权限
     */
    @RequiresPermissions("system:role:query")
    @GetMapping("/menuTree/{roleId}")
    public Result<List<Long>> menuTree(@PathVariable("roleId") Long roleId) {
        return Result.ok(roleService.getMenuIdsByRoleId(roleId));
    }

    /**
     * 修改角色菜单权限
     */
    @RequiresPermissions("system:role:edit")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authRole/selectAll")
    public Result<Void> updateAuthRole(@RequestParam Long roleId, @RequestParam Long[] menuIds) {
        roleService.updateAuthRole(roleId, menuIds);
        return Result.ok();
    }

    /**
     * 导出角色列表
     */
    @RequiresPermissions("system:role:export")
    @Log(title = "角色管理", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public Result<Void> export(
            @RequestParam(required = false) String roleCode,
            @RequestParam(required = false) String roleName,
            @RequestParam(required = false) Integer status) {
        List<RoleVO> list = roleService.selectRoleList(roleCode, roleName, status);
        roleService.exportRole(list);
        return Result.ok();
    }

    /**
     * 修改数据权限
     */
    @RequiresPermissions("system:role:edit")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping("/dataScope")
    public Result<Void> dataScope(@RequestBody RoleDTO dto) {
        roleService.updateDataScope(dto);
        return Result.ok();
    }
}
