package com.lest.modules.project.controller;

import com.lest.common.base.PageResult;
import com.lest.common.base.Result;
import com.lest.modules.project.entity.dto.IterationDTO;
import com.lest.modules.project.entity.vo.IterationVO;
import com.lest.modules.project.service.IterationService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 迭代控制器
 */
@Slf4j
@RestController
public class IterationController {

    private final IterationService iterationService;

    public IterationController(IterationService iterationService) {
        this.iterationService = iterationService;
    }

    /**
     * 创建迭代
     */
    @PostMapping("/project/{projectId}/iteration")
    public Result<Long> create(@PathVariable Long projectId, @Valid @RequestBody IterationDTO dto) {
        dto.setProjectId(projectId);
        return Result.ok(iterationService.create(dto));
    }

    /**
     * 分页查询迭代
     */
    @GetMapping("/project/{projectId}/iteration/page")
    public Result<PageResult<IterationVO>> page(
            @PathVariable Long projectId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.ok(iterationService.page(projectId, status, page, size));
    }

    /**
     * 获取迭代详情
     */
    @GetMapping("/iteration/{id}")
    public Result<IterationVO> getById(@PathVariable Long id) {
        return Result.ok(iterationService.getById(id));
    }

    /**
     * 更新迭代
     */
    @PutMapping("/iteration/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody IterationDTO dto) {
        dto.setId(id);
        iterationService.update(dto);
        return Result.ok();
    }

    /**
     * 删除迭代
     */
    @DeleteMapping("/iteration/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        iterationService.delete(id);
        return Result.ok();
    }

    /**
     * 启动迭代
     */
    @PutMapping("/iteration/{id}/start")
    public Result<Void> start(@PathVariable Long id) {
        iterationService.start(id);
        return Result.ok();
    }

    /**
     * 结束迭代
     */
    @PutMapping("/iteration/{id}/complete")
    public Result<Void> complete(@PathVariable Long id) {
        iterationService.complete(id);
        return Result.ok();
    }

    /**
     * 获取迭代任务列表
     */
    @GetMapping("/iteration/{id}/task")
    public Result<List<Long>> getTasks(@PathVariable Long id) {
        return Result.ok(iterationService.getTaskIds(id));
    }
}
