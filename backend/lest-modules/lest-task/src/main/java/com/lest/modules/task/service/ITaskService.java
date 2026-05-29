package com.lest.modules.task.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import com.lest.modules.task.domain.Task;
import com.lest.modules.task.domain.TaskCommit;
import com.lest.modules.task.domain.TaskDependency;
import com.lest.modules.task.domain.TaskWorklog;

/**
 * 任务 服务层
 * 
 * @author yshan2028
 */
public interface ITaskService
{
    /**
     * 查询任务列表
     */
    public List<Task> selectTaskList(Task task);

    /**
     * 查询任务详情
     */
    public Task selectTaskById(Long id);

    /**
     * 新增任务
     */
    public int insertTask(Task task);

    /**
     * 修改任务
     */
    public int updateTask(Task task);

    /**
     * 删除任务
     */
    public int deleteTaskById(Long id);

    /**
     * 更新任务状态
     */
    public int updateStatus(Long id, String status);

    /**
     * 分配任务
     */
    public int assignTask(Long id, Long assigneeId);

    /**
     * 认领任务
     */
    public int claimTask(Long id);

    /**
     * 获取看板数据
     */
    public List<Map<String, Object>> getBoard(Long projectId, Long iterationId);

    /**
     * 看板移动
     */
    public int moveTask(Long id, String targetColumn, Integer targetPosition);

    /**
     * 获取甘特图数据
     */
    public List<Task> getGantt(Long projectId, Long iterationId, Date startDate, Date endDate);

    /**
     * 创建子任务
     */
    public int insertSubtask(Long parentId, Task task);

    /**
     * 获取子任务列表
     */
    public List<Task> selectSubtasks(Long parentId);

    /**
     * 添加依赖
     */
    public int addDependency(Long taskId, TaskDependency dependency);

    /**
     * 获取依赖列表
     */
    public List<TaskDependency> selectDependencies(Long taskId);

    /**
     * 删除依赖
     */
    public int deleteDependency(Long taskId, Long depTaskId);

    /**
     * 添加工时
     */
    public int addWorklog(Long taskId, TaskWorklog worklog);

    /**
     * 获取工时列表
     */
    public List<TaskWorklog> selectWorklogs(Long taskId);

    /**
     * 获取关联提交
     */
    public List<TaskCommit> selectCommits(Long taskId);

    /**
     * 获取关联MR
     */
    public List<TaskCommit> selectMergeRequests(Long taskId);

    /**
     * 关联提交
     */
    public int addCommit(Long taskId, TaskCommit commit);

    /**
     * 关联MR
     */
    public int addMergeRequest(Long taskId, TaskCommit mr);
}
