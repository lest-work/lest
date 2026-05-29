package com.lest.modules.file.controller;

import com.lest.common.core.PageResult;
import com.lest.common.core.Result;
import com.lest.common.security.util.SecurityUtils;
import com.lest.modules.file.entity.domain.UserFile;
import com.lest.modules.file.entity.dto.UserFileDTO;
import com.lest.modules.file.entity.vo.UserFileVO;
import com.lest.modules.file.service.UserFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/user-file")
@RequiredArgsConstructor
public class UserFileController {

    private final UserFileService userFileService;

    @GetMapping("/list")
    public Result<PageResult<UserFileVO>> list(UserFile query) {
        Long currentUserId = SecurityUtils.getUserId();
        if (currentUserId != null && query.getUserId() == null) {
            query.setUserId(currentUserId);
        }
        return Result.ok(PageResult.of(userFileService.list(query)));
    }

    @GetMapping("/page")
    public Result<PageResult<UserFileVO>> page(UserFile query) {
        Long currentUserId = SecurityUtils.getUserId();
        if (currentUserId != null && query.getUserId() == null) {
            query.setUserId(currentUserId);
        }
        return Result.ok(PageResult.of(userFileService.page(query)));
    }

    @PostMapping
    public Result<Void> add(@RequestBody UserFileDTO dto) {
        Long currentUserId = SecurityUtils.getUserId();
        dto.setUserId(currentUserId);
        userFileService.add(dto);
        return Result.ok();
    }

    @PutMapping
    public Result<Void> update(@RequestBody UserFileDTO dto) {
        userFileService.update(dto);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        Long currentUserId = SecurityUtils.getUserId();
        userFileService.remove(id, currentUserId);
        return Result.ok();
    }

    @DeleteMapping("/batch")
    public Result<Void> removeBatch(@RequestBody List<Long> ids) {
        Long currentUserId = SecurityUtils.getUserId();
        userFileService.removeBatch(ids, currentUserId);
        return Result.ok();
    }
}
