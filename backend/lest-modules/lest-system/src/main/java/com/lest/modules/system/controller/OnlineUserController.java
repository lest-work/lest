package com.lest.modules.system.controller;

import com.lest.common.base.PageResult;
import com.lest.common.base.Result;
import com.lest.modules.system.entity.vo.OnlineUserVO;
import com.lest.modules.system.service.OnlineUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 在线用户控制器
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@RestController
@RequestMapping("/system/online-user")
@RequiredArgsConstructor
public class OnlineUserController {

    private final OnlineUserService onlineUserService;

    /**
     * 分页查询在线用户
     */
    @GetMapping("/page")
    public Result<PageResult<OnlineUserVO>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        return Result.ok(onlineUserService.page(page, size));
    }

    /**
     * 查询所有在线用户
     */
    @GetMapping("/list")
    public Result<List<OnlineUserVO>> list() {
        return Result.ok(onlineUserService.list());
    }

    /**
     * 获取在线用户数量
     */
    @GetMapping("/count")
    public Result<Long> count() {
        return Result.ok(onlineUserService.count());
    }

    /**
     * 踢出在线用户（单个或批量）
     * - 传入 sessionIds 时踢出指定用户
     * - 不传 sessionIds 时踢出全部用户
     */
    @PostMapping("/kickout")
    public Result<Void> kickout(@RequestParam(required = false) List<String> sessionIds) {
        onlineUserService.kickout(sessionIds);
        return Result.ok();
    }
}
