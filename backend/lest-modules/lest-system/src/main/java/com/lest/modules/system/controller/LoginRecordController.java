package com.lest.modules.system.controller;

import com.lest.common.base.PageResult;
import com.lest.common.base.Result;
import com.lest.modules.system.entity.dto.LoginLogDTO;
import com.lest.modules.system.entity.dto.LoginLogQueryDTO;
import com.lest.modules.system.entity.vo.LoginLogVO;
import com.lest.modules.system.service.LoginLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 登录日志控制器
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@RestController
@RequestMapping("/system/login-record")
@RequiredArgsConstructor
public class LoginRecordController {

    private final LoginLogService loginLogService;

    /** 分页查询登录日志 */
    @GetMapping("/page")
    public Result<PageResult<LoginLogVO>> page(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) Integer loginType,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        return Result.ok(loginLogService.page(new LoginLogQueryDTO(username, nickname, loginType, startTime, endTime, page, size)));
    }

    /** 查询登录日志列表（不分页，用于导出） */
    @GetMapping
    public Result<List<LoginLogVO>> list(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) Integer loginType,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        return Result.ok(loginLogService.page(new LoginLogQueryDTO(username, nickname, loginType, startTime, endTime, 1, 10000)).getRecords());
    }

    /** 根据ID查询登录日志详情 */
    @GetMapping("/{id}")
    public Result<LoginLogVO> getById(@PathVariable Long id) {
        return Result.ok(loginLogService.getById(id));
    }

    /**
     * 删除登录日志（单个或批量）
     * - 传入 ids 参数时批量删除
     * - 不传 ids 参数时删除指定 id 的记录
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @PathVariable Long id,
            @RequestParam(required = false) List<Long> ids) {
        loginLogService.delete(id, ids);
        return Result.ok();
    }

    /** 统计登录日志数量 */
    @GetMapping("/count")
    public Result<Long> count(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Integer loginType,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        return Result.ok(loginLogService.count(username, loginType, startTime, endTime));
    }

    /** 保存登录日志（供认证服务调用） */
    @PostMapping
    public Result<Void> save(@Valid @RequestBody LoginLogDTO dto) {
        loginLogService.save(dto);
        return Result.ok();
    }
}
