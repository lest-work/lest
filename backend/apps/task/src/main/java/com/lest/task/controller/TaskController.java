package com.lest.task.controller;

import java.util.List;
import java.util.Map;
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
import com.lest.common.security.annotation.RequireProjectRole;
import com.lest.task.domain.Task;
import com.lest.task.domain.TaskChangeHistory;
import com.lest.task.domain.TaskComment;
import com.lest.task.domain.TaskCommit;
import com.lest.task.domain.TaskDependency;
import com.lest.task.domain.TaskWorklog;
import com.lest.task.service.ITaskService;

/**
 * 任务管理
 *
 * @author yshan2028
 */
@RestController
@RequestMapping("/task")
public class TaskController extends BaseController
{
    private final ITaskService taskService;

    public TaskController(ITaskService taskService)
    {
        this.taskService = taskService;
    }

    @RequireProjectRole(checkMembership = true)
    @GetMapping("/list")
    public TableDataInfo list(Task task, @RequestParam(required = false) Long projectId)
    {
        startPage();
        List<Task> list = taskService.selectTaskList(task);
        return getDataTable(list);
    }

    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id)
    {
        return success(taskService.selectTaskById(id));
    }

    @Log(title = "任务管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Task task)
    {
        return toAjax(taskService.insertTask(task));
    }

    @Log(title = "任务管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Task task)
    {
        return toAjax(taskService.updateTask(task));
    }

    @Log(title = "任务管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable Long id)
    {
        return toAjax(taskService.deleteTaskById(id, com.lest.common.security.utils.SecurityUtils.getUserId()));
    }

    @RequireProjectRole(checkMembership = true)
    @GetMapping("/page")
    public TableDataInfo page(Task task, @RequestParam(required = false) Long projectId)
    {
        startPage();
        List<Task> list = taskService.selectTaskList(task);
        return getDataTable(list);
    }

    @Log(title = "任务状态", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}/status")
    public AjaxResult updateStatus(@PathVariable Long id, @RequestBody Task task)
    {
        return toAjax(taskService.updateStatus(id, task.getStatus()));
    }

    @Log(title = "任务分配", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}/assign")
    public AjaxResult assign(@PathVariable Long id, @RequestBody Task task)
    {
        return toAjax(taskService.assignTask(id, task.getAssigneeId()));
    }

    @PostMapping("/{id}/claim")
    public AjaxResult claim(@PathVariable Long id)
    {
        return toAjax(taskService.claimTask(id));
    }

    @RequireProjectRole(checkMembership = true)
    @GetMapping("/board/{projectId}")
    public AjaxResult board(@PathVariable Long projectId,
                            @RequestParam(required = false) Long iterationId)
    {
        return success(taskService.getBoard(projectId, iterationId));
    }

    @Log(title = "任务看板", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}/move")
    public AjaxResult move(@PathVariable Long id, @RequestBody Map<String, Object> params)
    {
        String targetColumn = (String) params.get("targetColumn");
        Integer targetPosition = params.get("targetPosition") != null
                ? ((Number) params.get("targetPosition")).intValue() : null;
        return toAjax(taskService.moveTask(id, targetColumn, targetPosition));
    }

    @RequireProjectRole(checkMembership = true)
    @GetMapping("/gantt")
    public AjaxResult gantt(@RequestParam(required = false) Long projectId,
                            @RequestParam(required = false) Long iterationId)
    {
        return success(taskService.getGantt(projectId, iterationId, null, null));
    }

    @Log(title = "子任务", businessType = BusinessType.INSERT)
    @PostMapping("/{id}/subtask")
    public AjaxResult addSubtask(@PathVariable Long id, @RequestBody Task task)
    {
        return toAjax(taskService.insertSubtask(id, task));
    }

    @GetMapping("/{id}/subtask/list")
    public AjaxResult subtaskList(@PathVariable Long id)
    {
        return success(taskService.selectSubtasks(id));
    }

    @PostMapping("/{id}/dependency")
    public AjaxResult addDependency(@PathVariable Long id, @RequestBody TaskDependency dependency)
    {
        return toAjax(taskService.addDependency(id, dependency));
    }

    @GetMapping("/{id}/dependency/list")
    public AjaxResult dependencyList(@PathVariable Long id)
    {
        return success(taskService.selectDependencies(id));
    }

    @DeleteMapping("/{id}/dependency/{depTaskId}")
    public AjaxResult removeDependency(@PathVariable Long id, @PathVariable Long depTaskId)
    {
        return toAjax(taskService.deleteDependency(id, depTaskId));
    }

    @Log(title = "任务工时", businessType = BusinessType.INSERT)
    @PostMapping("/{id}/worklog")
    public AjaxResult addWorklog(@PathVariable Long id, @RequestBody TaskWorklog worklog)
    {
        return toAjax(taskService.addWorklog(id, worklog));
    }

    @GetMapping("/{id}/worklog/list")
    public TableDataInfo worklogList(@PathVariable Long id)
    {
        startPage();
        List<TaskWorklog> list = taskService.selectWorklogs(id);
        return getDataTable(list);
    }

    @GetMapping("/{id}/commit/list")
    public AjaxResult commitList(@PathVariable Long id)
    {
        return success(taskService.selectCommits(id));
    }

    @GetMapping("/{id}/merge-request/list")
    public AjaxResult mergeRequestList(@PathVariable Long id)
    {
        return success(taskService.selectMergeRequests(id));
    }

    @GetMapping("/{id}/comment/list")
    public AjaxResult commentList(@PathVariable Long id)
    {
        return success(taskService.selectComments(id));
    }

    @Log(title = "任务评论", businessType = BusinessType.INSERT)
    @PostMapping("/{id}/comment")
    public AjaxResult addComment(@PathVariable Long id, @RequestBody TaskComment comment)
    {
        return toAjax(taskService.addComment(id, comment));
    }

    @Log(title = "任务评论", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}/comment/{commentId}")
    public AjaxResult updateComment(@PathVariable Long id, @PathVariable Long commentId, @RequestBody TaskComment comment)
    {
        comment.setTaskCommentId(commentId);
        return toAjax(taskService.updateComment(id, comment));
    }

    @Log(title = "任务评论", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}/comment/{commentId}")
    public AjaxResult removeComment(@PathVariable Long id, @PathVariable Long commentId)
    {
        return toAjax(taskService.deleteComment(id, commentId));
    }

    @PostMapping("/{id}/commit")
    public AjaxResult addCommit(@PathVariable Long id, @RequestBody TaskCommit commit)
    {
        return toAjax(taskService.addCommit(id, commit));
    }

    @PostMapping("/{id}/merge-request")
    public AjaxResult addMergeRequest(@PathVariable Long id, @RequestBody TaskCommit mr)
    {
        return toAjax(taskService.addMergeRequest(id, mr));
    }

    @Log(title = "预估工时", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}/estimate")
    public AjaxResult setEstimate(@PathVariable Long id, @RequestBody Map<String, Object> params)
    {
        return toAjax(taskService.updateTaskEstimate(id, params));
    }

    @Log(title = "剩余工时", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}/remaining")
    public AjaxResult setRemaining(@PathVariable Long id, @RequestBody Map<String, Object> params)
    {
        return toAjax(taskService.updateTaskRemaining(id, params));
    }

    @Log(title = "故事点", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}/storypoints")
    public AjaxResult setStoryPoints(@PathVariable Long id, @RequestBody Map<String, Object> params)
    {
        return toAjax(taskService.updateTaskStoryPoints(id, params));
    }

    @Log(title = "批量移动任务", businessType = BusinessType.UPDATE)
    @PutMapping("/batch/move-to-iteration")
    public AjaxResult batchMoveToIteration(@RequestBody Map<String, Object> params)
    {
        return toAjax(taskService.batchMoveToIteration(params));
    }

    @PostMapping("/{id}/watch")
    public AjaxResult watchTask(@PathVariable Long id)
    {
        int affected = taskService.watchTask(id);
        if (affected > 0)
        {
            return success(Map.of("watching", true));
        }
        return error("关注失败");
    }

    @DeleteMapping("/{id}/watch")
    public AjaxResult unwatchTask(@PathVariable Long id)
    {
        int affected = taskService.unwatchTask(id);
        if (affected > 0)
        {
            return success(Map.of("watching", false));
        }
        return error("取消关注失败");
    }

    @GetMapping("/watched")
    public AjaxResult getWatchedTaskIds()
    {
        return success(taskService.getWatchedTaskIds());
    }

    @GetMapping("/{id}/change-history")
    public AjaxResult getChangeHistory(@PathVariable Long id)
    {
        return success(taskService.getChangeHistory(id));
    }

    @GetMapping("/{id}/watchers")
    public AjaxResult getWatchers(@PathVariable Long id)
    {
        return success(taskService.getWatcherUsers(id));
    }

    @PostMapping("/{id}/clone")
    public AjaxResult cloneTask(@PathVariable Long id, @RequestBody Map<String, Object> params)
    {
        return success(taskService.cloneTask(id, params));
    }

    /**
     * 查询指定迭代下的任务列表（供 lest-project 通过 Feign 调用）
     */
    @GetMapping("/iteration/{iterationId}")
    public AjaxResult getByIteration(@PathVariable Long iterationId)
    {
        return success(taskService.selectTasksByIterationId(iterationId));
    }

    /**
     * 将源迭代中所有未完成的任务移入目标迭代（完成迭代时由 lest-project 调用）
     */
    @Log(title = "迭代任务迁移", businessType = BusinessType.UPDATE)
    @PutMapping("/iteration/{fromId}/move-to/{toId}")
    public AjaxResult moveUnfinishedToIteration(@PathVariable Long fromId, @PathVariable Long toId)
    {
        return toAjax(taskService.moveUnfinishedTasksToIteration(fromId, toId));
    }
}
