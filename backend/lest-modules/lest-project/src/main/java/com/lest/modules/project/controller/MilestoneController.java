package com.lest.modules.project.controller;

import com.lest.common.base.PageResult;
import com.lest.common.base.Result;
import com.lest.modules.project.entity.dto.MilestoneDTO;
import com.lest.modules.project.entity.dto.MilestoneIterationDTO;
import com.lest.modules.project.entity.vo.MilestoneVO;
import com.lest.modules.project.service.MilestoneService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 里程碑控制器
 */
@Slf4j
@RestController
public class MilestoneController {

    private final MilestoneService milestoneService;

    public MilestoneController(MilestoneService milestoneService) {
        this.milestoneService = milestoneService;
    }

    /**
     * 创建里程碑
     */
    @PostMapping("/project/{projectId}/milestone")
    public Result<Long> create(@PathVariable Long projectId, @Valid @RequestBody MilestoneDTO dto) {
        dto.setProjectId(projectId);
        return Result.ok(milestoneService.create(dto));
    }

    /**
     * 分页查询里程碑
     */
    @GetMapping("/project/{projectId}/milestone/page")
    public Result<PageResult<MilestoneVO>> page(
            @PathVariable Long projectId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.ok(milestoneService.page(projectId, page, size));
    }

    /**
     * 获取里程碑详情
     */
    @GetMapping("/milestone/{id}")
    public Result<MilestoneVO> getById(@PathVariable Long id) {
        return Result.ok(milestoneService.getById(id));
    }

    /**
     * 更新里程碑
     */
    @PutMapping("/milestone/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody MilestoneDTO dto) {
        dto.setId(id);
        milestoneService.update(dto);
        return Result.ok();
    }

    /**
     * 删除里程碑
     */
    @DeleteMapping("/milestone/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        milestoneService.delete(id);
        return Result.ok();
    }

    /**
     * 关联迭代到里程碑
     */
    @PostMapping("/milestone/{id}/iteration")
    public Result<Void> addIteration(@PathVariable Long id, @Valid @RequestBody MilestoneIterationDTO dto) {
        milestoneService.addIteration(id, dto);
        return Result.ok();
    }

    /**
     * 获取里程碑关联的迭代ID列表
     */
    @GetMapping("/milestone/{id}/iteration")
    public Result<List<Long>> getIterations(@PathVariable Long id) {
        return Result.ok(milestoneService.getIterationIds(id));
    }
}
