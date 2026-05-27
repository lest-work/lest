package com.lest.modules.system.controller;

import com.lest.common.base.PageResult;
import com.lest.common.base.Result;
import com.lest.modules.system.entity.dto.ConfigDTO;
import com.lest.modules.system.entity.vo.ConfigVO;
import com.lest.modules.system.service.ConfigService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 系统配置控制器
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@RestController
@RequestMapping("/system/config")
@RequiredArgsConstructor
public class ConfigController {

    private final ConfigService configService;

    /** 分页查询系统配置 */
    @GetMapping("/page")
    public Result<PageResult<ConfigVO>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String configGroup,
            @RequestParam(required = false) String configKey,
            @RequestParam(required = false) Integer status) {
        return Result.ok(configService.page(page, size, configGroup, configKey, status));
    }

    /** 查询配置列表（不分页，用于下拉选择等场景） */
    @GetMapping("/list")
    public Result<List<ConfigVO>> list(@RequestParam(required = false) String configGroup) {
        return Result.ok(configService.list(configGroup));
    }

    /** 根据ID查询配置详情 */
    @GetMapping("/{id}")
    public Result<ConfigVO> getById(@PathVariable Long id) {
        return Result.ok(configService.getById(id));
    }

    /** 根据配置键查询配置 */
    @GetMapping("/key/{configKey}")
    public Result<ConfigVO> getByKey(@PathVariable String configKey) {
        return Result.ok(configService.getByKey(configKey));
    }

    /** 根据分组查询所有启用的配置，返回键值对Map */
    @GetMapping("/group/{configGroup}")
    public Result<Map<String, String>> getByGroup(@PathVariable String configGroup) {
        return Result.ok(configService.getByGroup(configGroup));
    }

    /** 根据配置键获取配置值（快速接口） */
    @GetMapping("/value")
    public Result<String> getValue(@RequestParam String configKey,
                                    @RequestParam(defaultValue = "") String defaultValue) {
        return Result.ok(configService.getValue(configKey, defaultValue));
    }

    /** 创建配置 */
    @PostMapping
    public Result<Long> create(@Valid @RequestBody ConfigDTO dto) {
        return Result.ok(configService.create(dto));
    }

    /** 更新配置 */
    @PutMapping
    public Result<Void> update(@Valid @RequestBody ConfigDTO dto) {
        configService.update(dto);
        return Result.ok();
    }

    /** 删除配置 */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        configService.delete(id);
        return Result.ok();
    }
}
