package com.lest.modules.system.controller;

import com.lest.common.base.PageResult;
import com.lest.common.base.Result;
import com.lest.modules.system.entity.dto.LogQueryDTO;
import com.lest.modules.system.entity.vo.LogVO;
import com.lest.modules.system.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 操作日志控制器
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@RestController
@RequestMapping("/system/operation-record")
@RequiredArgsConstructor
public class OperationRecordController {

    private final LogService logService;

    /**
     * 分页查询操作日志
     */
    @GetMapping("/page")
    public Result<PageResult<LogVO>> page(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String createTimeStart,
            @RequestParam(required = false) String createTimeEnd,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        return Result.ok(logService.page(new LogQueryDTO(null, username, nickname, module, null, status, createTimeStart, createTimeEnd, page, size)));
    }

    /**
     * 查询操作日志列表（不分页，用于导出）
     */
    @GetMapping
    public Result<List<LogVO>> list(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String createTimeStart,
            @RequestParam(required = false) String createTimeEnd) {
        return Result.ok(logService.page(new LogQueryDTO(null, username, nickname, module, null, status, createTimeStart, createTimeEnd, 1, 10000)).getRecords());
    }

    /**
     * 根据ID查询操作日志详情
     */
    @GetMapping("/{id}")
    public Result<LogVO> getById(@PathVariable Long id) {
        return Result.ok(logService.getById(id));
    }
}
