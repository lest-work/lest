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

    @Autowired
    private TaskCommentMapper commentMapper;

    @Autowired
    private IssueLinkMapper issueLinkMapper;

    @Autowired
    private IssueLinkTypeMapper issueLinkTypeMapper;

    @Autowired
    private AttachmentMapper attachmentMapper;

    @Autowired
    private TaskVoteMapper taskVoteMapper;

    @Autowired
    private AutomationMapper automationMapper;

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
    public int deleteTaskById(Long taskId, Long deletedBy)
    {
        Task task = taskMapper.selectById(taskId);
        if (task == null)
        {
            throw new ServiceException("任务不存在");
        }
        // 子任务也需要级联软删除
        taskMapper.softDeleteChildren(taskId, deletedBy);
        taskLabelMapper.deleteByTaskId(taskId);
        watcherMapper.deleteByTaskId(taskId);
        return taskMapper.deleteById(taskId, deletedBy);
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addComment(Long taskId, TaskComment comment)
    {
        Task task = taskMapper.selectById(taskId);
        if (task == null)
        {
            throw new ServiceException("任务不存在");
        }
        comment.setTaskId(taskId);
        return commentMapper.insert(comment);
    }

    @Override
    public List<TaskComment> selectComments(Long taskId)
    {
        return commentMapper.selectByTaskId(taskId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteComment(Long taskId, Long commentId)
    {
        TaskComment comment = commentMapper.selectById(commentId);
        if (comment == null)
        {
            throw new ServiceException("评论不存在");
        }
        if (!comment.getTaskId().equals(taskId))
        {
            throw new ServiceException("评论不属于该任务");
        }
        return commentMapper.deleteById(commentId);
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

    // ===== Recycle Bin =====
    @Override
    public List<Task> selectDeletedTasks(Long projectId)
    {
        return taskMapper.selectDeletedList(projectId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int restoreTask(Long taskId)
    {
        Task task = taskMapper.selectById(taskId);
        if (task == null)
        {
            throw new ServiceException("任务不存在");
        }
        return taskMapper.restoreById(taskId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int permanentDeleteTask(Long taskId)
    {
        Task task = taskMapper.selectById(taskId);
        if (task == null)
        {
            throw new ServiceException("任务不存在");
        }
        // Clean up all related data
        taskLabelMapper.deleteByTaskId(taskId);
        watcherMapper.deleteByTaskId(taskId);
        worklogMapper.deleteByTaskId(taskId);
        commentMapper.deleteByTaskId(taskId);
        commitMapper.deleteByTaskId(taskId);
        dependencyMapper.deleteByTaskId(taskId);
        issueLinkMapper.deleteByTaskId(taskId);
        taskVoteMapper.deleteByTaskId(taskId);
        attachmentMapper.selectByTaskId(taskId).forEach(a -> attachmentMapper.permanentDeleteById(a.getAttachmentId()));
        return taskMapper.permanentDeleteById(taskId);
    }

    // ===== Issue Link =====
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addIssueLink(Long taskId, IssueLink link)
    {
        Task task = taskMapper.selectById(taskId);
        if (task == null) throw new ServiceException("任务不存在");
        Task target = taskMapper.selectById(link.getTargetTaskId());
        if (target == null) throw new ServiceException("目标任务不存在");
        if (taskId.equals(link.getTargetTaskId())) throw new ServiceException("不能关联自身");

        link.setSourceTaskId(taskId);
        link.setCreatedBy(com.lest.common.security.utils.SecurityUtils.getUserId());
        return issueLinkMapper.insert(link);
    }

    @Override
    public List<IssueLink> selectIssueLinks(Long taskId)
    {
        return issueLinkMapper.selectLinksWithDetails(taskId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int removeIssueLink(Long linkId)
    {
        return issueLinkMapper.deleteById(linkId);
    }

    @Override
    public List<IssueLinkType> selectIssueLinkTypes()
    {
        return issueLinkTypeMapper.selectAllActive();
    }

    // ===== Attachment =====
    @Override
    public List<Attachment> selectAttachments(Long taskId)
    {
        return attachmentMapper.selectByTaskId(taskId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int uploadAttachment(Long taskId, Attachment attachment)
    {
        Task task = taskMapper.selectById(taskId);
        if (task == null) throw new ServiceException("任务不存在");
        attachment.setTaskId(taskId);
        attachment.setUploadedBy(com.lest.common.security.utils.SecurityUtils.getUserId());
        Long maxVersion = attachmentMapper.getMaxVersionByFileName(taskId, attachment.getFileName());
        attachment.setVersion(maxVersion != null ? maxVersion + 1 : 1);
        return attachmentMapper.insert(attachment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteAttachment(Long attachmentId)
    {
        Long userId = com.lest.common.security.utils.SecurityUtils.getUserId();
        return attachmentMapper.softDeleteById(attachmentId, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int restoreAttachment(Long attachmentId)
    {
        return attachmentMapper.restoreById(attachmentId);
    }

    // ===== Vote =====
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int voteTask(Long taskId)
    {
        Task task = taskMapper.selectById(taskId);
        if (task == null) throw new ServiceException("任务不存在");
        Long userId = com.lest.common.security.utils.SecurityUtils.getUserId();
        if (taskVoteMapper.selectByTaskAndUser(taskId, userId) != null)
            throw new ServiceException("已投票");
        TaskVote vote = new TaskVote();
        vote.setTaskId(taskId);
        vote.setUserId(userId);
        return taskVoteMapper.insert(vote);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int unvoteTask(Long taskId)
    {
        Long userId = com.lest.common.security.utils.SecurityUtils.getUserId();
        return taskVoteMapper.deleteByTaskAndUser(taskId, userId);
    }

    @Override
    public int getVoteCount(Long taskId)
    {
        return taskVoteMapper.countByTaskId(taskId);
    }

    @Override
    public List<TaskVote> selectVoters(Long taskId)
    {
        return taskVoteMapper.selectByTaskId(taskId);
    }

    // ===== Automation =====
    @Override
    public List<AutomationRule> selectAutomationRules(Long projectId)
    {
        return automationMapper.selectRuleList(projectId);
    }

    @Override
    public AutomationRule selectAutomationRuleById(Long ruleId)
    {
        return automationMapper.selectRuleById(ruleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int createAutomationRule(AutomationRule rule)
    {
        rule.setCreatedBy(com.lest.common.security.utils.SecurityUtils.getUserId());
        return automationMapper.insertRule(rule);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateAutomationRule(AutomationRule rule)
    {
        return automationMapper.updateRule(rule);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteAutomationRule(Long ruleId)
    {
        return automationMapper.deleteRuleById(ruleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int toggleAutomationRule(Long ruleId, Integer isEnabled)
    {
        return automationMapper.toggleRuleEnabled(ruleId, isEnabled);
    }

    @Override
    public List<AutomationExecutionLog> selectAutomationLogs(Long ruleId)
    {
        return automationMapper.selectLogList(ruleId);
    }

    @Override
    public List<AutomationExecutionLog> selectAutomationLogsByTask(Long taskId)
    {
        return automationMapper.selectLogListByTaskId(taskId);
    }

    // ===== Time Tracking =====
    @Override
    public int updateTaskEstimate(Long taskId, Map<String, Object> params)
    {
        Task task = taskMapper.selectById(taskId);
        if (task == null) throw new ServiceException("任务不存在");
        Object val = params.get("estimatedHours");
        if (val == null) throw new ServiceException("预估工时不能为空");
        BigDecimal hours = new BigDecimal(val.toString());
        task.setEstimatedHours(hours);
        task.setRemainingHours(hours);
        return taskMapper.updateById(task);
    }

    @Override
    public int updateTaskRemaining(Long taskId, Map<String, Object> params)
    {
        Task task = taskMapper.selectById(taskId);
        if (task == null) throw new ServiceException("任务不存在");
        Object val = params.get("remainingHours");
        if (val == null) throw new ServiceException("剩余工时不能为空");
        task.setRemainingHours(new BigDecimal(val.toString()));
        return taskMapper.updateById(task);
    }

    @Override
    public int updateTaskStoryPoints(Long taskId, Map<String, Object> params)
    {
        Task task = taskMapper.selectById(taskId);
        if (task == null) throw new ServiceException("任务不存在");
        Object val = params.get("storyPoints");
        if (val != null)
        {
            task.setStoryPoints(new BigDecimal(val.toString()));
        }
        return taskMapper.updateById(task);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchMoveToIteration(Map<String, Object> params)
    {
        @SuppressWarnings("unchecked")
        List<Long> taskIds = (List<Long>) params.get("taskIds");
        Long iterationId = Long.valueOf(params.get("iterationId").toString());
        if (taskIds == null || taskIds.isEmpty()) return 0;
        int count = 0;
        for (Long taskId : taskIds)
        {
            Task task = taskMapper.selectById(taskId);
            if (task != null)
            {
                task.setIterationId(iterationId);
                count += taskMapper.updateById(task);
            }
        }
        return count;
    }
}
