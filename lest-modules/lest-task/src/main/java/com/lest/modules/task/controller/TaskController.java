package com.lest.modules.task.controller;

import com.lest.common.core.PageResult;
import com.lest.common.core.Result;
import com.lest.modules.task.entity.dto.*;
import com.lest.modules.task.entity.vo.*;
import com.lest.modules.task.service.TaskService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 任务控制器
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Slf4j
@RestController
@RequestMapping("/task")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * 创建任务
     */
    @PostMapping
    public Result<Long> create(@Valid @RequestBody TaskDTO dto) {
        log.info("创建任务: title={}", dto.getTitle());
        return Result.ok(taskService.create(dto));
    }

    /**
     * 分页查询任务
     */
    @GetMapping("/page")
    public Result<PageResult<TaskVO>> page(
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) Long iterationId,
            @RequestParam(required = false) Long assigneeId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) List<Long> labels,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.ok(taskService.page(projectId, iterationId, assigneeId, status, priority, labels, keyword, page, size));
    }

    /**
     * 获取任务详情
     */
    @GetMapping("/{id}")
    public Result<TaskVO> getById(@PathVariable Long id) {
        return Result.ok(taskService.getById(id));
    }

    /**
     * 更新任务
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody TaskDTO dto) {
        dto.setId(id);
        log.info("更新任务: taskId={}", id);
        taskService.update(dto);
        return Result.ok();
    }

    /**
     * 删除任务
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        log.info("删除任务: taskId={}", id);
        taskService.delete(id);
        return Result.ok();
    }

    /**
     * 更新任务状态
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody StatusDTO dto) {
        log.info("更新任务状态: taskId={}, status={}", id, dto.getStatus());
        taskService.updateStatus(id, dto.getStatus());
        return Result.ok();
    }

    /**
     * 分配任务
     */
    @PutMapping("/{id}/assign")
    public Result<Void> assign(@PathVariable Long id, @Valid @RequestBody AssignDTO dto) {
        log.info("分配任务: taskId={}, assigneeId={}", id, dto.getAssigneeId());
        taskService.assign(id, dto);
        return Result.ok();
    }

    /**
     * 认领任务
     */
    @PostMapping("/{id}/claim")
    public Result<Void> claim(@PathVariable Long id) {
        log.info("认领任务: taskId={}", id);
        taskService.claim(id);
        return Result.ok();
    }

    /**
     * 获取看板视图
     */
    @GetMapping("/board")
    public Result<List<BoardVO>> board(
            @RequestParam Long projectId,
            @RequestParam(required = false) Long iterationId) {
        return Result.ok(taskService.getBoard(projectId, iterationId));
    }

    /**
     * 看板拖拽移动
     */
    @PutMapping("/{id}/move")
    public Result<Void> move(@PathVariable Long id, @Valid @RequestBody MoveDTO dto) {
        log.info("移动任务: taskId={}, to={}", id, dto.getTargetColumn());
        taskService.move(id, dto);
        return Result.ok();
    }

    /**
     * 获取甘特图数据
     */
    @GetMapping("/gantt")
    public Result<List<GanttVO>> gantt(
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) Long iterationId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return Result.ok(taskService.getGantt(projectId, iterationId, startDate, endDate));
    }

    /**
     * 添加子任务
     */
    @PostMapping("/{id}/subtask")
    public Result<Long> createSubtask(@PathVariable Long id, @Valid @RequestBody TaskDTO dto) {
        log.info("添加子任务: parentId={}", id);
        return Result.ok(taskService.createSubtask(id, dto));
    }

    /**
     * 获取子任务列表
     */
    @GetMapping("/{id}/subtask")
    public Result<PageResult<TaskVO>> getSubtasks(@PathVariable Long id) {
        return Result.ok(PageResult.of(taskService.getSubtasks(id), (long) taskService.getSubtasks(id).size(), 1, (int) Math.min(taskService.getSubtasks(id).size(), 100)));
    }

    /**
     * 添加依赖
     */
    @PostMapping("/{id}/dependency")
    public Result<Void> addDependency(@PathVariable Long id, @Valid @RequestBody DependencyDTO dto) {
        log.info("添加依赖: taskId={}, depTaskId={}", id, dto.getDependencyTaskId());
        taskService.addDependency(id, dto);
        return Result.ok();
    }

    /**
     * 获取依赖
     */
    @GetMapping("/{id}/dependency")
    public Result<PageResult<TaskDependencyVO>> getDependencies(@PathVariable Long id) {
        return Result.ok(PageResult.of(taskService.getDependencies(id), (long) taskService.getDependencies(id).size(), 1, (int) Math.min(taskService.getDependencies(id).size(), 100)));
    }

    /**
     * 删除依赖
     */
    @DeleteMapping("/{id}/dependency/{depTaskId}")
    public Result<Void> deleteDependency(@PathVariable Long id, @PathVariable Long depTaskId) {
        log.info("删除依赖: taskId={}, depTaskId={}", id, depTaskId);
        taskService.deleteDependency(id, depTaskId);
        return Result.ok();
    }

    /**
     * 添加工时
     */
    @PostMapping("/{id}/worklog")
    public Result<Long> addWorklog(@PathVariable Long id, @Valid @RequestBody WorklogDTO dto) {
        log.info("添加工时: taskId={}, hours={}", id, dto.getHours());
        return Result.ok(taskService.addWorklog(id, dto));
    }

    /**
     * 获取工时记录
     */
    @GetMapping("/{id}/worklog")
    public Result<PageResult<WorklogVO>> getWorklogs(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.ok(taskService.getWorklogs(id, page, size));
    }

    /**
     * 获取关联提交
     */
    @GetMapping("/{id}/commits")
    public Result<PageResult<CommitVO>> getCommits(@PathVariable Long id) {
        return Result.ok(PageResult.of(taskService.getCommits(id), (long) taskService.getCommits(id).size(), 1, (int) Math.min(taskService.getCommits(id).size(), 100)));
    }

    /**
     * 获取关联MR
     */
    @GetMapping("/{id}/merge-requests")
    public Result<PageResult<MergeRequestVO>> getMergeRequests(@PathVariable Long id) {
        return Result.ok(PageResult.of(taskService.getMergeRequests(id), (long) taskService.getMergeRequests(id).size(), 1, (int) Math.min(taskService.getMergeRequests(id).size(), 100)));
    }

    /**
     * 手动关联提交
     */
    @PostMapping("/{id}/commit")
    public Result<Void> addCommit(@PathVariable Long id, @Valid @RequestBody CommitDTO dto) {
        log.info("手动关联提交: taskId={}, commitHash={}", id, dto.getCommitHash());
        taskService.addCommit(id, dto);
        return Result.ok();
    }

    /**
     * 手动关联MR
     */
    @PostMapping("/{id}/merge-request")
    public Result<Void> addMergeRequest(@PathVariable Long id, @Valid @RequestBody MergeRequestDTO dto) {
        log.info("手动关联MR: taskId={}, mrId={}", id, dto.getMrId());
        taskService.addMergeRequest(id, dto);
        return Result.ok();
    }
}
