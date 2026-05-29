package com.lest.modules.system.controller;

import com.lest.common.core.PageResult;
import com.lest.common.core.Result;
import com.lest.modules.system.entity.dto.NoticeDTO;
import com.lest.modules.system.entity.dto.NoticeQueryDTO;
import com.lest.modules.system.entity.vo.NoticeVO;
import com.lest.modules.system.service.NoticeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping("/list")
    public Result<PageResult<NoticeVO>> list(NoticeQueryDTO dto) {
        return Result.ok(noticeService.page(dto));
    }

    @GetMapping("/{id}")
    public Result<NoticeVO> getById(@PathVariable Long id) {
        return Result.ok(noticeService.getById(id));
    }

    @PostMapping
    public Result<Void> add(@Valid @RequestBody NoticeDTO dto) {
        noticeService.add(dto);
        return Result.ok();
    }

    @PutMapping
    public Result<Void> update(@Valid @RequestBody NoticeDTO dto) {
        noticeService.update(dto);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        noticeService.delete(id);
        return Result.ok();
    }

    @PutMapping("/{id}/publish")
    public Result<Void> publish(@PathVariable Long id) {
        noticeService.publish(id);
        return Result.ok();
    }
}
