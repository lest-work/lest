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
    public List<Task> selectTaskList(Task task);

    public Task selectTaskById(Long taskId);

    public int insertTask(Task task);

    public int updateTask(Task task);

    public int deleteTaskById(Long taskId);

    public int updateStatus(Long taskId, String status);

    public int assignTask(Long taskId, Long assigneeId);

    public int claimTask(Long taskId);

    public List<Map<String, Object>> getBoard(Long projectId, Long iterationId);

    public int moveTask(Long taskId, String targetColumn, Integer targetPosition);

    public List<Task> getGantt(Long projectId, Long iterationId, Date startDate, Date endDate);

    public int insertSubtask(Long parentId, Task task);

    public List<Task> selectSubtasks(Long parentId);

    public int addDependency(Long taskId, TaskDependency dependency);

    public List<TaskDependency> selectDependencies(Long taskId);

    public int deleteDependency(Long taskId, Long depTaskId);

    public int addWorklog(Long taskId, TaskWorklog worklog);

    public List<TaskWorklog> selectWorklogs(Long taskId);

    public List<TaskCommit> selectCommits(Long taskId);

    public List<TaskCommit> selectMergeRequests(Long taskId);

    public int addCommit(Long taskId, TaskCommit commit);

    public int addMergeRequest(Long taskId, TaskCommit mr);
}
