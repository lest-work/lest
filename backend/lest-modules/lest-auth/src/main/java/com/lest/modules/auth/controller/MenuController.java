package com.lest.modules.auth.controller;

import com.lest.common.base.Result;
import com.lest.modules.auth.entity.dto.MenuDTO;
import com.lest.modules.auth.entity.vo.MenuVO;
import com.lest.modules.auth.entity.vo.RouteVO;
import com.lest.modules.auth.service.MenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单控制器
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Slf4j
@RestController
@RequestMapping("/auth/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    /** 获取菜单树（用于菜单管理页面） */
    @GetMapping("/tree")
    public Result<List<MenuVO>> getTree() {
        return Result.ok(menuService.getTree());
    }

    /** 获取路由列表（用于前端动态路由加载） */
    @GetMapping("/routes")
    public Result<List<RouteVO>> getRoutes() {
        return Result.ok(menuService.getRoutes());
    }

    /** 查询所有菜单（不分页） */
    @GetMapping
    public Result<List<MenuVO>> list() {
        return Result.ok(menuService.listAll());
    }

    /** 根据ID查询菜单 */
    @GetMapping("/{id}")
    public Result<MenuVO> getById(@PathVariable Long id) {
        return Result.ok(menuService.getById(id));
    }

    /** 创建菜单 */
    @PostMapping
    public Result<Long> create(@Valid @RequestBody MenuDTO dto) {
        log.info("创建菜单: menuName={}", dto.getMenuName());
        return Result.ok(menuService.create(dto));
    }

    /** 更新菜单 */
    @PutMapping
    public Result<Void> update(@Valid @RequestBody MenuDTO dto) {
        log.info("更新菜单: menuId={}", dto.getId());
        menuService.update(dto);
        return Result.ok();
    }

    /**
     * 删除菜单（单个或批量）
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
        menuService.delete(id, idList);
        return Result.ok();
    }
}
