package com.lest.modules.task.service.impl;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.lest.common.core.exception.ServiceException;
import com.lest.modules.task.domain.*;
import com.lest.modules.task.mapper.*;
import com.lest.modules.task.service.ITaskService;

/**
 * 任务 服务层实现
 *
 * @author yshan2028
 */
@Service
public class TaskServiceImpl implements ITaskService
{
    private static final int MAX_DEPTH = 3;
    private static final List<String> STATUS_FLOW = List.of("todo", "in_progress", "completed");

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private TaskWorklogMapper worklogMapper;

    @Autowired
    private TaskLabelMapper taskLabelMapper;

    @Autowired
    private TaskDependencyMapper dependencyMapper;

    @Autowired
    private TaskWatcherMapper watcherMapper;

    @Autowired
    private TaskCommitMapper commitMapper;

    @Override
    public List<Task> selectTaskList(Task task)
    {
        List<Task> list = taskMapper.selectTaskList(
                task.getProjectId(), task.getIterationId(), task.getAssigneeId(),
                task.getStatus(), task.getPriority(), task.getTitle());
        list.forEach(this::enrichTask);
        return list;
    }

    @Override
    public Task selectTaskById(Long taskId)
    {
        Task task = taskMapper.selectById(taskId);
        if (task != null)
        {
            enrichTask(task);
        }
        return task;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertTask(Task task)
    {
        if (task.getParentId() != null)
        {
            int depth = calculateDepth(task.getParentId());
            if (depth >= MAX_DEPTH)
            {
                throw new ServiceException("子任务嵌套层数不能超过" + MAX_DEPTH + "层");
            }
        }
        task.setStatus("todo");
        if (task.getSort() == null)
        {
            Integer maxSort = task.getParentId() != null
                    ? taskMapper.getMaxSortByParentId(task.getParentId())
                    : taskMapper.getMaxSortByProjectId(task.getProjectId());
            task.setSort(maxSort != null ? maxSort + 1 : 0);
        }
        int rows = taskMapper.insert(task);
        if (task.getLabelIds() != null && !task.getLabelIds().isEmpty())
        {
            saveLabels(task.getTaskId(), task.getLabelIds());
        }
        if (task.getWatcherIds() != null && !task.getWatcherIds().isEmpty())
        {
            saveWatchers(task.getTaskId(), task.getWatcherIds());
        }
        else
        {
            Long currentUserId = com.lest.common.security.utils.SecurityUtils.getUserId();
            if (currentUserId != null)
            {
                saveWatchers(task.getTaskId(), List.of(currentUserId));
            }
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateTask(Task task)
    {
        Task existing = taskMapper.selectById(task.getTaskId());
        if (existing == null)
        {
            throw new ServiceException("任务不存在");
        }
        int rows = taskMapper.updateById(task);
        if (task.getLabelIds() != null)
        {
            taskLabelMapper.deleteByTaskId(task.getTaskId());
            saveLabels(task.getTaskId(), task.getLabelIds());
        }
        if (task.getWatcherIds() != null)
        {
            watcherMapper.deleteByTaskId(task.getTaskId());
            saveWatchers(task.getTaskId(), task.getWatcherIds());
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteTaskById(Long taskId)
    {
        Task task = taskMapper.selectById(taskId);
        if (task == null)
        {
            throw new ServiceException("任务不存在");
        }
        int childCount = taskMapper.countByParentId(taskId);
        if (childCount > 0)
        {
            throw new ServiceException("任务下存在子任务，无法删除");
        }
        int depCount = dependencyMapper.countByTaskId(taskId);
        if (depCount > 0)
        {
            throw new ServiceException("任务存在依赖关系，无法删除");
        }
        taskLabelMapper.deleteByTaskId(taskId);
        watcherMapper.deleteByTaskId(taskId);
        return taskMapper.deleteById(taskId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateStatus(Long taskId, String status)
    {
        Task task = taskMapper.selectById(taskId);
        if (task == null)
        {
            throw new ServiceException("任务不存在");
        }
        if ("completed".equals(status))
        {
            checkBlockers(taskId);
        }
        task.setStatus(status);
        if ("completed".equals(status))
        {
            task.setCompletedAt(new Date());
        }
        else if ("in_progress".equals(status) && task.getStartTime() == null)
        {
            task.setStartTime(new Date());
        }
        return taskMapper.updateById(task);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int assignTask(Long taskId, Long assigneeId)
    {
        Task task = taskMapper.selectById(taskId);
        if (task == null)
        {
            throw new ServiceException("任务不存在");
        }
        task.setAssigneeId(assigneeId);
        return taskMapper.updateById(task);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int claimTask(Long taskId)
    {
        Task task = taskMapper.selectById(taskId);
        if (task == null)
        {
            throw new ServiceException("任务不存在");
        }
        Long currentUserId = com.lest.common.security.utils.SecurityUtils.getUserId();
        if (currentUserId == null)
        {
            throw new ServiceException("无法获取当前用户");
        }
        task.setAssigneeId(currentUserId);
        return taskMapper.updateById(task);
    }

    @Override
    public List<Map<String, Object>> getBoard(Long projectId, Long iterationId)
    {
        List<Task> tasks = taskMapper.selectBoardTasks(projectId, iterationId);
        tasks.forEach(this::enrichTask);

        Map<String, List<Task>> grouped = tasks.stream()
                .collect(Collectors.groupingBy(Task::getStatus));

        List<Map<String, Object>> result = new ArrayList<>();
        for (String status : STATUS_FLOW)
        {
            List<Task> columnTasks = grouped.getOrDefault(status, List.of());
            Map<String, Object> column = new HashMap<>();
            column.put("column", status);
            column.put("tasks", columnTasks);
            column.put("totalCount", columnTasks.size());
            result.add(column);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int moveTask(Long taskId, String targetColumn, Integer targetPosition)
    {
        Task task = taskMapper.selectById(taskId);
        if (task == null)
        {
            throw new ServiceException("任务不存在");
        }
        if ("completed".equals(targetColumn))
        {
            checkBlockers(taskId);
        }
        task.setStatus(targetColumn);
        if (targetPosition != null)
        {
            task.setSort(targetPosition);
        }
        return taskMapper.updateById(task);
    }

    @Override
    public List<Task> getGantt(Long projectId, Long iterationId, Date startDate, Date endDate)
    {
        return taskMapper.selectGanttTasks(projectId, iterationId, startDate, endDate);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSubtask(Long parentId, Task task)
    {
        Task parent = taskMapper.selectById(parentId);
        if (parent == null)
        {
            throw new ServiceException("父任务不存在");
        }
        int depth = calculateDepth(parentId);
        if (depth >= MAX_DEPTH)
        {
            throw new ServiceException("子任务嵌套层数不能超过" + MAX_DEPTH + "层");
        }
        task.setParentId(parentId);
        return insertTask(task);
    }

    @Override
    public List<Task> selectSubtasks(Long parentId)
    {
        List<Task> list = taskMapper.selectByParentId(parentId);
        list.forEach(this::enrichTask);
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addDependency(Long taskId, TaskDependency dependency)
    {
        Task task = taskMapper.selectById(taskId);
        if (task == null)
        {
            throw new ServiceException("任务不存在");
        }
        Task depTask = taskMapper.selectById(dependency.getDependencyTaskId());
        if (depTask == null)
        {
            throw new ServiceException("依赖任务不存在");
        }
        if (taskId.equals(dependency.getDependencyTaskId()))
        {
            throw new ServiceException("不能依赖自身");
        }
        if (hasCircularDependency(taskId, dependency.getDependencyTaskId()))
        {
            throw new ServiceException("存在循环依赖");
        }
        dependency.setTaskId(taskId);
        return dependencyMapper.insert(dependency);
    }

    @Override
    public List<TaskDependency> selectDependencies(Long taskId)
    {
        List<Long> depIds = dependencyMapper.selectDependencyTaskIds(taskId);
        List<Task> depTasks = depIds.isEmpty() ? List.of() : taskMapper.selectByIds(depIds);
        Map<Long, Task> taskMap = depTasks.stream().collect(Collectors.toMap(Task::getTaskId, t -> t));

        return depIds.stream().map(depId -> {
            TaskDependency dep = new TaskDependency();
            dep.setTaskId(taskId);
            dep.setDependencyTaskId(depId);
            dep.setType("blocker");
            Task dt = taskMap.get(depId);
            if (dt != null)
            {
                dep.setDependencyTaskTitle(dt.getTitle());
                dep.setDependencyTaskStatus(dt.getStatus());
            }
            return dep;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteDependency(Long taskId, Long depTaskId)
    {
        return dependencyMapper.deleteByTaskIdAndDependencyTaskId(taskId, depTaskId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addWorklog(Long taskId, TaskWorklog worklog)
    {
        Task task = taskMapper.selectById(taskId);
        if (task == null)
        {
            throw new ServiceException("任务不存在");
        }
        if (worklog.getHours() == null || worklog.getHours().compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException("工时必须大于0");
        }
        Long currentUserId = com.lest.common.security.utils.SecurityUtils.getUserId();
        worklog.setTaskId(taskId);
        worklog.setUserId(currentUserId);
        int rows = worklogMapper.insert(worklog);

        BigDecimal totalHours = task.getActualHours() != null ? task.getActualHours() : BigDecimal.ZERO;
        task.setActualHours(totalHours.add(worklog.getHours()));
        taskMapper.updateById(task);
        return rows;
    }

    @Override
    public List<TaskWorklog> selectWorklogs(Long taskId)
    {
        return worklogMapper.selectByTaskId(taskId);
    }

    @Override
    public List<TaskCommit> selectCommits(Long taskId)
    {
        return commitMapper.selectCommitsByTaskId(taskId);
    }

    @Override
    public List<TaskCommit> selectMergeRequests(Long taskId)
    {
        return commitMapper.selectMRsByTaskId(taskId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addCommit(Long taskId, TaskCommit commit)
    {
        Task task = taskMapper.selectById(taskId);
        if (task == null)
        {
            throw new ServiceException("任务不存在");
        }
        commit.setTaskId(taskId);
        commit.setType("commit");
        commit.setSource("manual");
        return commitMapper.insert(commit);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addMergeRequest(Long taskId, TaskCommit mr)
    {
        Task task = taskMapper.selectById(taskId);
        if (task == null)
        {
            throw new ServiceException("任务不存在");
        }
        mr.setTaskId(taskId);
        mr.setType("mr");
        mr.setSource("manual");
        return commitMapper.insert(mr);
    }

    private void enrichTask(Task task)
    {
        task.setWatcherIds(watcherMapper.selectWatcherIdsByTaskId(task.getTaskId()));
        task.setChildCount(taskMapper.countByParentId(task.getTaskId()));
        task.setHasBlockers(!dependencyMapper.selectDependencyTaskIds(task.getTaskId()).isEmpty());
        task.setDepth(calculateDepth(task.getParentId()));
    }

    private void checkBlockers(Long taskId)
    {
        List<Long> blockers = dependencyMapper.selectDependencyTaskIds(taskId);
        if (!blockers.isEmpty())
        {
            List<Task> blockerTasks = taskMapper.selectByIds(blockers);
            boolean hasIncomplete = blockerTasks.stream()
                    .anyMatch(t -> !"completed".equals(t.getStatus()));
            if (hasIncomplete)
            {
                throw new ServiceException("存在未完成的前置任务");
            }
        }
    }

    private boolean hasCircularDependency(Long taskId, Long newDepId)
    {
        Set<Long> visited = new HashSet<>();
        Queue<Long> queue = new LinkedList<>();
        queue.add(newDepId);
        while (!queue.isEmpty())
        {
            Long current = queue.poll();
            if (current.equals(taskId))
            {
                return true;
            }
            if (visited.contains(current))
            {
                continue;
            }
            visited.add(current);
            queue.addAll(dependencyMapper.selectDependencyTaskIds(current));
        }
        return false;
    }

    private int calculateDepth(Long parentId)
    {
        int depth = 0;
        Long currentId = parentId;
        while (currentId != null && depth < MAX_DEPTH)
        {
            Task parent = taskMapper.selectById(currentId);
            if (parent == null) break;
            currentId = parent.getParentId();
            depth++;
        }
        return depth;
    }

    private void saveLabels(Long taskId, List<Long> labelIds)
    {
        for (Long labelId : labelIds)
        {
            TaskLabel tl = new TaskLabel();
            tl.setTaskId(taskId);
            tl.setLabelId(labelId);
            taskLabelMapper.insert(tl);
        }
    }

    private void saveWatchers(Long taskId, List<Long> userIds)
    {
        for (Long userId : userIds)
        {
            TaskWatcher tw = new TaskWatcher();
            tw.setTaskId(taskId);
            tw.setUserId(userId);
            watcherMapper.insert(tw);
        }
    }
}
