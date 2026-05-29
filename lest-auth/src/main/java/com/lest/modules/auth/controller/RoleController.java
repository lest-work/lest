package com.lest.modules.auth.controller;

import com.lest.common.core.PageResult;
import com.lest.common.core.Result;
import com.lest.modules.auth.entity.dto.RoleDTO;
import com.lest.modules.auth.entity.vo.RoleVO;
import com.lest.modules.auth.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色控制器
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Slf4j
@RestController
@RequestMapping("/auth/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    /** 分页查询角色 */
    @GetMapping("/page")
    public Result<PageResult<RoleVO>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String roleCode,
            @RequestParam(required = false) String roleName,
            @RequestParam(required = false) Integer status) {
        return Result.ok(roleService.page(page, size, roleCode, roleName, status));
    }

    /** 查询所有角色（统一返回 PageResult 格式） */
    @GetMapping
    public Result<PageResult<RoleVO>> list() {
        return Result.ok(PageResult.of(roleService.listAll()));
    }

    /** 根据ID查询角色 */
    @GetMapping("/{id}")
    public Result<RoleVO> getById(@PathVariable Long id) {
        return Result.ok(roleService.getById(id));
    }

    /** 创建角色 */
    @PostMapping
    public Result<Long> create(@Valid @RequestBody RoleDTO dto) {
        log.info("创建角色: roleCode={}", dto.getRoleCode());
        return Result.ok(roleService.create(dto));
    }

    /** 更新角色 */
    @PutMapping
    public Result<Void> update(@Valid @RequestBody RoleDTO dto) {
        log.info("更新角色: roleId={}", dto.getId());
        roleService.update(dto);
        return Result.ok();
    }

    /**
     * 删除角色（单个或批量）
     * - 传入 ids 参数时批量删除（支持逗号分隔字符串，如 "1,2,3"）
     * - 不传 ids 参数时删除指定 id 的记录
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @PathVariable Long id,
            @RequestParam(required = false) String ids) {
        List<Long> idList = null;
        if (StringUtils.hasText(ids)) {
            idList = Arrays.stream(ids.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
        }
        roleService.delete(id, idList);
        return Result.ok();
    }
}
