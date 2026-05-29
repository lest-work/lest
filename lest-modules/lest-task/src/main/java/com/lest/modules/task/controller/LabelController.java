package com.lest.modules.task.controller;

import com.lest.common.core.PageResult;
import com.lest.common.core.Result;
import com.lest.modules.task.entity.dto.LabelDTO;
import com.lest.modules.task.entity.vo.LabelVO;
import com.lest.modules.task.service.LabelService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 标签控制器
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Slf4j
@RestController
@RequestMapping("/project")
public class LabelController {

    private final LabelService labelService;

    public LabelController(LabelService labelService) {
        this.labelService = labelService;
    }

    /**
     * 获取项目标签列表
     */
    @GetMapping("/{projectId}/labels")
    public Result<PageResult<LabelVO>> getLabels(@PathVariable Long projectId) {
        return Result.ok(PageResult.of(labelService.getByProjectId(projectId), (long) labelService.getByProjectId(projectId).size(), 1, (int) Math.min(labelService.getByProjectId(projectId).size(), 100)));
    }

    /**
     * 创建标签
     */
    @PostMapping("/{projectId}/label")
    public Result<Long> create(@PathVariable Long projectId, @Valid @RequestBody LabelDTO dto) {
        log.info("创建标签: projectId={}, name={}", projectId, dto.getName());
        return Result.ok(labelService.create(projectId, dto));
    }

    /**
     * 删除标签
     */
    @DeleteMapping("/label/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        log.info("删除标签: labelId={}", id);
        labelService.delete(id);
        return Result.ok();
    }
}
