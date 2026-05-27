package com.lest.modules.auth.controller;

import com.lest.common.base.PageResult;
import com.lest.common.base.Result;
import com.lest.modules.auth.entity.dto.UserDTO;
import com.lest.modules.auth.entity.vo.UserVO;
import com.lest.modules.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户控制器
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Slf4j
@RestController
@RequestMapping("/auth/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 分页查询用户
     *
     * @param page    页码
     * @param size    每页大小
     * @param username 用户名（模糊搜索）
     * @param phone   手机号（模糊搜索）
     * @param status  状态
     * @param orgId   机构ID
     * @return 分页结果
     */
    @GetMapping("/page")
    public Result<PageResult<UserVO>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long orgId) {
        return Result.ok(userService.page(page, size, username, phone, status, orgId));
    }

    /**
     * 获取所有用户（不分页）
     *
     * @return 用户列表
     */
    @GetMapping("/list")
    public Result<List<UserVO>> list() {
        return Result.ok(userService.listAll());
    }

    /**
     * 根据ID查询用户
     *
     * @param id 用户ID
     * @return 用户VO
     */
    @GetMapping("/{id}")
    public Result<UserVO> getById(@PathVariable Long id) {
        return Result.ok(userService.getById(id));
    }

    /**
     * 获取用户角色ID列表
     *
     * @param id 用户ID
     * @return 角色ID列表
     */
    @GetMapping("/{id}/roles")
    public Result<List<Long>> getRoleIds(@PathVariable Long id) {
        return Result.ok(userService.getRoleIds(id));
    }

    /**
     * 创建用户
     *
     * @param dto 用户DTO
     * @return 用户ID
     */
    @PostMapping
    public Result<Long> create(@Valid @RequestBody UserDTO dto) {
        log.info("创建用户: username={}", dto.getUsername());
        return Result.ok(userService.create(dto));
    }

    /**
     * 更新用户
     *
     * @param dto 用户DTO
     * @return 操作结果
     */
    @PutMapping
    public Result<Void> update(@Valid @RequestBody UserDTO dto) {
        log.info("更新用户: userId={}", dto.getId());
        userService.update(dto);
        return Result.ok();
    }

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        log.info("删除用户: userId={}", id);
        userService.delete(id);
        return Result.ok();
    }

    /**
     * 修改用户状态
     *
     * @param id     用户ID
     * @param status 状态
     * @return 操作结果
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        log.info("修改用户状态: userId={}, status={}", id, status);
        userService.updateStatus(id, status);
        return Result.ok();
    }

    /**
     * 重置用户密码
     *
     * @param id 用户ID
     * @return 操作结果
     */
    @PutMapping("/{id}/password")
    public Result<Void> resetPassword(@PathVariable Long id) {
        log.info("重置用户密码: userId={}", id);
        userService.resetPassword(id);
        return Result.ok();
    }

    /**
     * 分配用户角色
     *
     * @param id      用户ID
     * @param roleIds 角色ID数组
     * @return 操作结果
     */
    @PutMapping("/{id}/roles")
    public Result<Void> assignRoles(@PathVariable Long id, @RequestBody Long[] roleIds) {
        log.info("分配用户角色: userId={}, roleIds={}", id, roleIds);
        userService.assignRoles(id, roleIds);
        return Result.ok();
    }
}
