package com.lest.modules.task.controller;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.lest.common.core.web.controller.BaseController;
import com.lest.common.core.web.domain.AjaxResult;
import com.lest.common.core.web.page.TableDataInfo;
import com.lest.common.log.annotation.Log;
import com.lest.common.log.enums.BusinessType;
import com.lest.common.security.annotation.RequiresPermissions;
import com.lest.modules.task.domain.Task;
import com.lest.modules.task.domain.TaskComment;
import com.lest.modules.task.domain.TaskCommit;
import com.lest.modules.task.domain.TaskDependency;
import com.lest.modules.task.domain.TaskWorklog;
import com.lest.modules.task.domain.Attachment;
import com.lest.modules.task.domain.IssueLink;
import com.lest.modules.task.service.ITaskService;

/**
 * 任务管理
 * 
 * @author yshan2028
 */
@RestController
@RequestMapping("")
public class TaskController extends BaseController
{
    @Autowired
    private ITaskService taskService;

    /**
     * 查询任务列表
     */
    @RequiresPermissions("task:task:list")
    @GetMapping("/list")
    public TableDataInfo list(Task task)
    {
        startPage();
        List<Task> list = taskService.selectTaskList(task);
        return getDataTable(list);
    }

    /**
     * 获取任务详情
     */
    @RequiresPermissions("task:task:query")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id)
    {
        return success(taskService.selectTaskById(id));
    }

    /**
     * 新增任务
     */
    @RequiresPermissions("task:task:add")
    @Log(title = "任务管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Task task)
    {
        return toAjax(taskService.insertTask(task));
    }

    /**
     * 修改任务
     */
    @RequiresPermissions("task:task:edit")
    @Log(title = "任务管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Task task)
    {
        return toAjax(taskService.updateTask(task));
    }

    /**
     * 删除任务（软删除）
     */
    @RequiresPermissions("task:task:remove")
    @Log(title = "任务管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable Long id)
    {
        return toAjax(taskService.deleteTaskById(id, com.lest.common.security.utils.SecurityUtils.getUserId()));
    }

    /**
     * 更新任务状态
     */
    @RequiresPermissions("task:task:edit")
    @Log(title = "任务状态", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}/status")
    public AjaxResult updateStatus(@PathVariable Long id, @RequestBody Task task)
    {
        return toAjax(taskService.updateStatus(id, task.getStatus()));
    }

    /**
     * 分配任务
     */
    @RequiresPermissions("task:task:edit")
    @Log(title = "任务分配", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}/assign")
    public AjaxResult assign(@PathVariable Long id, @RequestBody Task task)
    {
        return toAjax(taskService.assignTask(id, task.getAssigneeId()));
    }

    /**
     * 认领任务
     */
    @PostMapping("/{id}/claim")
    public AjaxResult claim(@PathVariable Long id)
    {
        return toAjax(taskService.claimTask(id));
    }

    /**
     * 获取看板视图
     */
    @RequiresPermissions("task:task:list")
    @GetMapping("/board")
    public AjaxResult board(@RequestParam Long projectId,
                            @RequestParam(required = false) Long iterationId)
    {
        return success(taskService.getBoard(projectId, iterationId));
    }

    /**
     * 看板拖拽移动
     */
    @RequiresPermissions("task:task:edit")
    @Log(title = "任务看板", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}/move")
    public AjaxResult move(@PathVariable Long id, @RequestBody Map<String, Object> params)
    {
        String targetColumn = (String) params.get("targetColumn");
        Integer targetPosition = params.get("targetPosition") != null
                ? ((Number) params.get("targetPosition")).intValue() : null;
        return toAjax(taskService.moveTask(id, targetColumn, targetPosition));
    }

    /**
     * 获取甘特图数据
     */
    @RequiresPermissions("task:task:list")
    @GetMapping("/gantt")
    public AjaxResult gantt(@RequestParam(required = false) Long projectId,
                            @RequestParam(required = false) Long iterationId)
    {
        return success(taskService.getGantt(projectId, iterationId, null, null));
    }

    /**
     * 添加子任务
     */
    @RequiresPermissions("task:task:add")
    @Log(title = "子任务", businessType = BusinessType.INSERT)
    @PostMapping("/{id}/subtask")
    public AjaxResult addSubtask(@PathVariable Long id, @RequestBody Task task)
    {
        return toAjax(taskService.insertSubtask(id, task));
    }

    /**
     * 获取子任务列表
     */
    @GetMapping("/{id}/subtask/list")
    public AjaxResult subtaskList(@PathVariable Long id)
    {
        return success(taskService.selectSubtasks(id));
    }

    /**
     * 添加依赖
     */
    @PostMapping("/{id}/dependency")
    public AjaxResult addDependency(@PathVariable Long id, @RequestBody TaskDependency dependency)
    {
        return toAjax(taskService.addDependency(id, dependency));
    }

    /**
     * 获取依赖
     */
    @GetMapping("/{id}/dependency/list")
    public AjaxResult dependencyList(@PathVariable Long id)
    {
        return success(taskService.selectDependencies(id));
    }

    /**
     * 删除依赖
     */
    @DeleteMapping("/{id}/dependency/{depTaskId}")
    public AjaxResult removeDependency(@PathVariable Long id, @PathVariable Long depTaskId)
    {
        return toAjax(taskService.deleteDependency(id, depTaskId));
    }

    /**
     * 添加工时
     */
    @RequiresPermissions("task:task:edit")
    @Log(title = "任务工时", businessType = BusinessType.INSERT)
    @PostMapping("/{id}/worklog")
    public AjaxResult addWorklog(@PathVariable Long id, @RequestBody TaskWorklog worklog)
    {
        return toAjax(taskService.addWorklog(id, worklog));
    }

    /**
     * 获取工时记录
     */
    @GetMapping("/{id}/worklog/list")
    public TableDataInfo worklogList(@PathVariable Long id)
    {
        startPage();
        List<TaskWorklog> list = taskService.selectWorklogs(id);
        return getDataTable(list);
    }

    /**
     * 获取关联提交
     */
    @GetMapping("/{id}/commit/list")
    public AjaxResult commitList(@PathVariable Long id)
    {
        return success(taskService.selectCommits(id));
    }

    /**
     * 获取关联MR
     */
    @GetMapping("/{id}/merge-request/list")
    public AjaxResult mergeRequestList(@PathVariable Long id)
    {
        return success(taskService.selectMergeRequests(id));
    }

    /**
     * 获取任务评论列表
     */
    @GetMapping("/{id}/comment/list")
    public AjaxResult commentList(@PathVariable Long id)
    {
        return success(taskService.selectComments(id));
    }

    /**
     * 新增任务评论
     */
    @RequiresPermissions("task:task:edit")
    @Log(title = "任务评论", businessType = BusinessType.INSERT)
    @PostMapping("/{id}/comment")
    public AjaxResult addComment(@PathVariable Long id, @RequestBody TaskComment comment)
    {
        return toAjax(taskService.addComment(id, comment));
    }

    /**
     * 删除任务评论
     */
    @RequiresPermissions("task:task:remove")
    @Log(title = "任务评论", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}/comment/{commentId}")
    public AjaxResult removeComment(@PathVariable Long id, @PathVariable Long commentId)
    {
        return toAjax(taskService.deleteComment(id, commentId));
    }

    /**
     * 手动关联提交
     */
    @PostMapping("/{id}/commit")
    public AjaxResult addCommit(@PathVariable Long id, @RequestBody TaskCommit commit)
    {
        return toAjax(taskService.addCommit(id, commit));
    }

    /**
     * 手动关联MR
     */
    @PostMapping("/{id}/merge-request")
    public AjaxResult addMergeRequest(@PathVariable Long id, @RequestBody TaskCommit mr)
    {
        return toAjax(taskService.addMergeRequest(id, mr));
    }

    // ===== Time Tracking =====
    /**
     * 设置预估工时
     */
    @RequiresPermissions("task:task:edit")
    @Log(title = "预估工时", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}/estimate")
    public AjaxResult setEstimate(@PathVariable Long id, @RequestBody Map<String, Object> params)
    {
        return toAjax(taskService.updateTaskEstimate(id, params));
    }

    /**
     * 设置剩余工时
     */
    @RequiresPermissions("task:task:edit")
    @Log(title = "剩余工时", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}/remaining")
    public AjaxResult setRemaining(@PathVariable Long id, @RequestBody Map<String, Object> params)
    {
        return toAjax(taskService.updateTaskRemaining(id, params));
    }

    /**
     * 设置故事点
     */
    @RequiresPermissions("task:task:edit")
    @Log(title = "故事点", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}/storypoints")
    public AjaxResult setStoryPoints(@PathVariable Long id, @RequestBody Map<String, Object> params)
    {
        return toAjax(taskService.updateTaskStoryPoints(id, params));
    }

    /**
     * 批量移动任务到迭代
     */
    @RequiresPermissions("task:task:edit")
    @Log(title = "批量移动任务", businessType = BusinessType.UPDATE)
    @PutMapping("/batch/move-to-iteration")
    public AjaxResult batchMoveToIteration(@RequestBody Map<String, Object> params)
    {
        return toAjax(taskService.batchMoveToIteration(params));
    }
}
