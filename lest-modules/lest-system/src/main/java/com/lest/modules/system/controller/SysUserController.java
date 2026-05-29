package com.lest.modules.system.controller;

import com.lest.common.core.PageResult;
import com.lest.common.core.Result;
import com.lest.common.core.web.controller.BaseController;
import com.lest.common.core.web.page.TableDataInfo;
import com.lest.common.log.annotation.Log;
import com.lest.common.log.enums.BusinessType;
import com.lest.modules.system.entity.dto.UserDTO;
import com.lest.modules.system.entity.vo.UserVO;
import com.lest.modules.system.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.lest.common.security.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 用户管理 Controller
 *
 * @author yshan2028
 * @since 2026-05-28
 */
@Slf4j
@RestController
@RequestMapping("/system/user")
@RequiredArgsConstructor
public class SysUserController extends BaseController {

    private final UserService userService;

    /**
     * 分页查询用户列表
     */
    @RequiresPermissions("system:user:list")
    @GetMapping("/list")
    public TableDataInfo list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long orgId) {
        startPage();
        List<UserVO> list = userService.selectUserList(username, nickname, phone, email, status, orgId);
        return getDataTable(list);
    }

    /**
     * 获取用户详情
     */
    @RequiresPermissions("system:user:query")
    @GetMapping("/{userId}")
    public Result<UserVO> getById(@PathVariable("userId") Long userId) {
        return Result.ok(userService.getById(userId));
    }

    /**
     * 新增用户
     */
    @RequiresPermissions("system:user:add")
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    @PostMapping
    public Result<Long> create(@Valid @RequestBody UserDTO dto) {
        return Result.ok(userService.createUser(dto));
    }

    /**
     * 修改用户
     */
    @RequiresPermissions("system:user:edit")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result<Void> update(@Valid @RequestBody UserDTO dto) {
        userService.updateUser(dto);
        return Result.ok();
    }

    /**
     * 删除用户
     */
    @RequiresPermissions("system:user:remove")
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{userId}")
    public Result<Void> delete(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return Result.ok();
    }

    /**
     * 导出用户列表
     */
    @RequiresPermissions("system:user:export")
    @Log(title = "用户管理", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public Result<Void> export(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long orgId) {
        List<UserVO> list = userService.selectUserList(username, nickname, phone, email, status, orgId);
        userService.exportUser(list);
        return Result.ok();
    }

    /**
     * 下载导入模板
     */
    @RequiresPermissions("system:user:import")
    @GetMapping("/importTemplate")
    public Result<Void> importTemplate() {
        userService.importTemplate();
        return Result.ok();
    }

    /**
     * 导入用户数据
     */
    @RequiresPermissions("system:user:import")
    @Log(title = "用户管理", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public Result<String> importData(MultipartFile file, boolean updateSupport) throws Exception {
        String msg = userService.importUser(file, updateSupport);
        Result<String> result = Result.ok();
        result.setData(msg);
        return result;
    }

    /**
     * 重置用户密码
     */
    @RequiresPermissions("system:user:resetPwd")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/resetPwd")
    public Result<Void> resetPwd(@RequestParam Long userId, @RequestParam String password) {
        userService.resetPassword(userId, password);
        return Result.ok();
    }

    /**
     * 修改用户状态
     */
    @RequiresPermissions("system:user:edit")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public Result<Void> changeStatus(@RequestParam Long userId, @RequestParam Integer status) {
        userService.updateUserStatus(userId, status);
        return Result.ok();
    }

    /**
     * 获取用户菜单权限
     */
    @RequiresPermissions("system:user:query")
    @GetMapping("/authRole/{userId}")
    public Result<UserVO> authRole(@PathVariable("userId") Long userId) {
        UserVO user = userService.getById(userId);
        user.setRoles(userService.getUserRoleIds(userId));
        return Result.ok(user);
    }

    /**
     * 取消授权
     */
    @RequiresPermissions("system:user:edit")
    @Log(title = "用户管理", businessType = BusinessType.GRANT)
    @PutMapping("/authRole/cancel")
    public Result<Void> cancelAuthRole(@RequestParam Long userId, @RequestParam Long roleId) {
        userService.cancelAuthRole(userId, roleId);
        return Result.ok();
    }

    /**
     * 批量取消授权
     */
    @RequiresPermissions("system:user:edit")
    @Log(title = "用户管理", businessType = BusinessType.GRANT)
    @PutMapping("/authRole/cancelAll")
    public Result<Void> cancelAuthRoleAll(@RequestParam Long userId, @RequestParam Long[] roleIds) {
        userService.cancelAuthRoleAll(userId, roleIds);
        return Result.ok();
    }

    /**
     * 选择授权
     */
    @RequiresPermissions("system:user:edit")
    @Log(title = "用户管理", businessType = BusinessType.GRANT)
    @PutMapping("/authRole/selectAll")
    public Result<Void> selectAuthRole(@RequestParam Long userId, @RequestParam Long[] roleIds) {
        userService.selectAuthRole(userId, roleIds);
        return Result.ok();
    }

    /**
     * 获取所有用户（用于下拉选择）
     */
    @RequiresPermissions("system:user:list")
    @GetMapping("/all")
    public Result<List<UserVO>> listAll() {
        return Result.ok(userService.selectUserList(null, null, null, null, null, null));
    }
}
