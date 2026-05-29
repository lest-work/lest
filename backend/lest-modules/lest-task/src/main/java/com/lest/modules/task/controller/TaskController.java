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
import com.lest.modules.task.domain.Task;
import com.lest.modules.task.domain.TaskCommit;
import com.lest.modules.task.domain.TaskDependency;
import com.lest.modules.task.domain.TaskWorklog;
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
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id)
    {
        return success(taskService.selectTaskById(id));
    }

    /**
     * 新增任务
     */
    @PostMapping
    public AjaxResult add(@RequestBody Task task)
    {
        return toAjax(taskService.insertTask(task));
    }

    /**
     * 修改任务
     */
    @PutMapping
    public AjaxResult edit(@RequestBody Task task)
    {
        return toAjax(taskService.updateTask(task));
    }

    /**
     * 删除任务
     */
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable Long id)
    {
        return toAjax(taskService.deleteTaskById(id));
    }

    /**
     * 更新任务状态
     */
    @PutMapping("/{id}/status")
    public AjaxResult updateStatus(@PathVariable Long id, @RequestBody Task task)
    {
        return toAjax(taskService.updateStatus(id, task.getStatus()));
    }

    /**
     * 分配任务
     */
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
    @GetMapping("/board")
    public AjaxResult board(@RequestParam Long projectId,
                            @RequestParam(required = false) Long iterationId)
    {
        return success(taskService.getBoard(projectId, iterationId));
    }

    /**
     * 看板拖拽移动
     */
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
    @GetMapping("/gantt")
    public AjaxResult gantt(@RequestParam(required = false) Long projectId,
                            @RequestParam(required = false) Long iterationId)
    {
        return success(taskService.getGantt(projectId, iterationId, null, null));
    }

    /**
     * 添加子任务
     */
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
}
