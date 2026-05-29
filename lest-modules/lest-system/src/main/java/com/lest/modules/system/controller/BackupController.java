package com.lest.modules.system.controller;

import com.lest.common.core.PageResult;
import com.lest.common.core.Result;
import com.lest.modules.system.entity.dto.BackupDTO;
import com.lest.modules.system.entity.vo.BackupVO;
import com.lest.modules.system.service.BackupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据备份控制器
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@RestController
@RequestMapping("/system/backup")
@RequiredArgsConstructor
public class BackupController {

    private final BackupService backupService;

    /** 分页查询备份记录 */
    @GetMapping("/page")
    public Result<PageResult<BackupVO>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String backupType) {
        return Result.ok(backupService.page(page, size, status, backupType));
    }

    /** 查询所有备份记录 */
    @GetMapping("/list")
    public Result<PageResult<BackupVO>> list() {
        return Result.ok(PageResult.of(backupService.list()));
    }

    /** 根据ID查询备份详情 */
    @GetMapping("/{id}")
    public Result<BackupVO> getById(@PathVariable Long id) {
        return Result.ok(backupService.getById(id));
    }

    /** 创建备份记录 */
    @PostMapping
    public Result<Long> create(@Valid @RequestBody BackupDTO dto) {
        return Result.ok(backupService.create(dto));
    }

    /** 触发立即备份任务 */
    @PostMapping("/trigger")
    public Result<Void> trigger(@RequestParam(required = false) String backupType) {
        backupService.triggerBackup(backupType);
        return Result.ok();
    }

    /** 从指定备份恢复数据 */
    @PostMapping("/restore/{id}")
    public Result<Void> restore(@PathVariable Long id) {
        backupService.restore(id);
        return Result.ok();
    }

    /** 删除备份记录 */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        backupService.delete(id);
        return Result.ok();
    }
}
