package com.lest.task.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.lest.api.system.RemoteUserService;
import com.lest.api.system.domain.SysUser;
import com.lest.api.system.model.LoginUser;
import com.lest.common.core.constant.SecurityConstants;
import com.lest.common.core.domain.R;
import com.lest.common.core.exception.ServiceException;
import com.lest.common.mq.constants.MqTopicConstants;
import com.lest.common.security.permission.ProjectRoleChecker;
import com.lest.common.security.utils.ProjectAuthHelper;
import com.lest.common.security.utils.SecurityUtils;
import com.lest.task.domain.*;
import com.lest.task.mapper.*;
import com.lest.task.service.ITaskService;

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
    /** 简单的 @提及 匹配规则：@ 后跟邮箱或账号字符串 */
    private static final Pattern MENTION_PATTERN = Pattern.compile("@([A-Za-z0-9_.+\\-@]+)");

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
    private TaskChangeHistoryMapper changeHistoryMapper;

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
    private LabelMapper labelMapper;

    @Autowired
    private AutomationMapper automationMapper;

    @Autowired(required = false)
    private RemoteUserService remoteUserService;

    @Autowired(required = false)
    private ProjectRoleChecker projectRoleChecker;

    @Autowired(required = false)
    private org.springframework.kafka.core.KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private String toJson(Object obj)
    {
        try { return objectMapper.writeValueAsString(obj); }
        catch (Exception e) { return "{}"; }
    }

    @Override
    public List<Task> selectTaskList(Task task)
    {
        List<Task> list = taskMapper.selectTaskList(
                task.getProjectId(), task.getIterationId(), task.getAssigneeId(),
                task.getStatus(), task.getPriority(), task.getTitle());
        list.forEach(this::enrichTask);
        return list;
    }

    private void checkProjectMembership(Task task)
    {
        checkProjectMembership(task.getProjectId());
    }

    private void checkProjectMembership(Long projectId)
    {
        if (projectRoleChecker != null && projectId != null)
        {
            ProjectAuthHelper.checkMembership(projectRoleChecker, projectId);
        }
    }

    private Task requireTask(Long taskId)
    {
        Task task = taskMapper.selectById(taskId);
        if (task == null)
        {
            throw new ServiceException("任务不存在");
        }
        return task;
    }

    private Task requireTaskWithMembership(Long taskId)
    {
        Task task = requireTask(taskId);
        checkProjectMembership(task.getProjectId());
        return task;
    }

    /**
     * 从评论内容中提取被提及的原始标记（例如 @user@example.com）。
     */
    private Set<String> extractMentions(String content)
    {
        if (content == null || content.isEmpty())
        {
            return Collections.emptySet();
        }
        Set<String> result = new HashSet<>();
        Matcher matcher = MENTION_PATTERN.matcher(content);
        while (matcher.find())
        {
            String token = matcher.group(1);
            if (token == null || token.isEmpty())
            {
                continue;
            }
            // 去掉结尾常见标点
            token = token.replaceAll("[,.;:!?)]*$", "");
            if (!token.isEmpty())
            {
                result.add(token);
            }
        }
        return result;
    }

    /**
     * 解析被提及的用户 ID 列表：
     * <ul>
     *   <li>如果 token 看起来像邮箱（包含 "@" 且包含 "."），优先通过 {@link RemoteUserService#getUserByEmail(String, String)} 按邮箱解析；</li>
     *   <li>否则回退为用户名（loginName），通过 {@link RemoteUserService#getUserInfo(String, String)} 解析；</li>
     *   <li>任一方式解析成功并获得 userId 后加入结果集，最终去重返回。</li>
     * </ul>
     */
    private List<Long> resolveMentionedUserIds(Set<String> rawMentions)
    {
        if (remoteUserService == null || rawMentions == null || rawMentions.isEmpty())
        {
            return Collections.emptyList();
        }
        List<Long> ids = new ArrayList<>();
        for (String token : rawMentions)
        {
            try
            {
                boolean resolved = false;
                String lookupToken = token;
                // 1) 若像邮箱，优先取 @ 前面的本地部分作为用户名进行解析
                if (token.contains("@"))
                {
                    String localPart = token.substring(0, token.indexOf('@'));
                    if (!localPart.isEmpty())
                    {
                        lookupToken = localPart;
                    }
                }

                // 2) 按用户名(loginName) 尝试解析
                R<LoginUser> resp = remoteUserService.getUserInfo(lookupToken, SecurityConstants.INNER);
                    if (resp == null || resp.getCode() == R.FAIL || resp.getData() == null)
                    {
                        continue;
                    }
                    LoginUser user = resp.getData();
                    if (user.getUserid() != null)
                    {
                        ids.add(user.getUserid());
                    }
            }
            catch (Exception ignored)
            {
                // 远程用户服务失败时跳过该提及，避免影响整体评论流程
            }
        }
        return ids.stream().distinct().collect(Collectors.toList());
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
        checkProjectMembership(task);
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
        if (rows > 0 && (task.getTaskNo() == null || task.getTaskNo().isEmpty()))
        {
            String prefix = task.getProjectId() != null ? ("P" + task.getProjectId()) : "TASK";
            task.setTaskNo(prefix + "-" + task.getTaskId());
            taskMapper.updateById(task);
        }
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
        Task existing = requireTaskWithMembership(task.getTaskId());
        if (task.getProjectId() == null)
        {
            task.setProjectId(existing.getProjectId());
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
        Task task = requireTaskWithMembership(taskId);
        taskMapper.softDeleteChildren(taskId, deletedBy);
        taskLabelMapper.deleteByTaskId(taskId);
        watcherMapper.deleteByTaskId(taskId);
        return taskMapper.deleteById(taskId, deletedBy);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateStatus(Long taskId, String status)
    {
        Task task = requireTaskWithMembership(taskId);
        if ("completed".equals(status))
        {
            checkBlockers(taskId);
        }
        String oldStatus = task.getStatus();
        task.setStatus(status);
        if ("completed".equals(status))
        {
            task.setCompletedAt(new Date());
        }
        else if ("in_progress".equals(status) && task.getStartTime() == null)
        {
            task.setStartTime(new Date());
        }
        int rows = taskMapper.updateById(task);
        if (rows > 0 && kafkaTemplate != null)
        {
            Map<String, Object> event = new HashMap<>();
            event.put("taskId", taskId);
            event.put("taskTitle", task.getTitle());
            event.put("oldStatus", oldStatus);
            event.put("newStatus", status);
            event.put("assigneeId", task.getAssigneeId());
            kafkaTemplate.send(MqTopicConstants.TASK_STATUS_CHANGED, String.valueOf(taskId), toJson(event));
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int assignTask(Long taskId, Long assigneeId)
    {
        Task task = requireTaskWithMembership(taskId);
        task.setAssigneeId(assigneeId);
        int rows = taskMapper.updateById(task);
        if (rows > 0 && kafkaTemplate != null)
        {
            Map<String, Object> event = new HashMap<>();
            event.put("taskId", taskId);
            event.put("taskTitle", task.getTitle());
            event.put("assigneeId", assigneeId);
            kafkaTemplate.send(MqTopicConstants.TASK_ASSIGNEE_CHANGED, String.valueOf(taskId), toJson(event));
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int claimTask(Long taskId)
    {
        Task task = requireTaskWithMembership(taskId);
        Long currentUserId = SecurityUtils.getUserId();
        if (currentUserId == null)
        {
            throw new ServiceException("无法获取当前用户");
        }
        task.setAssigneeId(currentUserId);
        int rows = taskMapper.updateById(task);
        if (rows > 0 && kafkaTemplate != null)
        {
            Map<String, Object> event = new HashMap<>();
            event.put("taskId", taskId);
            event.put("taskTitle", task.getTitle());
            event.put("assigneeId", currentUserId);
            kafkaTemplate.send(MqTopicConstants.TASK_ASSIGNEE_CHANGED, String.valueOf(taskId), toJson(event));
        }
        return rows;
    }

    @Override
    public List<Map<String, Object>> getBoard(Long projectId, Long iterationId)
    {
        List<Task> tasks = taskMapper.selectBoardTasks(projectId, iterationId);
        tasks.forEach(this::enrichTask);

        Map<String, List<Task>> grouped = tasks.stream()
                .collect(Collectors.groupingBy(Task::getStatus));

        // PRD V1.0: columns format = {id, name, status, taskCount, tasks}
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, String> statusNames = Map.of(
                "todo", "待办",
                "in_progress", "进行中",
                "completed", "已完成"
        );
        int idx = 1;
        for (String status : STATUS_FLOW)
        {
            List<Task> columnTasks = grouped.getOrDefault(status, List.of());
            Map<String, Object> column = new HashMap<>();
            column.put("id", idx++);
            column.put("name", statusNames.getOrDefault(status, status));
            column.put("status", status);
            column.put("taskCount", columnTasks.size());
            column.put("tasks", columnTasks);
            result.add(column);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int moveTask(Long taskId, String targetColumn, Integer targetPosition)
    {
        Task task = requireTaskWithMembership(taskId);
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
        Task parent = requireTaskWithMembership(parentId);
        int depth = calculateDepth(parentId);
        if (depth >= MAX_DEPTH)
        {
            throw new ServiceException("子任务嵌套层数不能超过" + MAX_DEPTH + "层");
        }
        task.setParentId(parentId);
        if (task.getProjectId() == null)
        {
            task.setProjectId(parent.getProjectId());
        }
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
        requireTaskWithMembership(taskId);
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
        requireTaskWithMembership(taskId);
        return dependencyMapper.deleteByTaskIdAndDependencyTaskId(taskId, depTaskId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addWorklog(Long taskId, TaskWorklog worklog)
    {
        Task task = requireTaskWithMembership(taskId);
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
        requireTaskWithMembership(taskId);
        commit.setTaskId(taskId);
        commit.setType("commit");
        commit.setSource("manual");
        return commitMapper.insert(commit);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addMergeRequest(Long taskId, TaskCommit mr)
    {
        requireTaskWithMembership(taskId);
        mr.setTaskId(taskId);
        mr.setType("mr");
        mr.setSource("manual");
        return commitMapper.insert(mr);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addComment(Long taskId, TaskComment comment)
    {
        Task task = requireTaskWithMembership(taskId);
        Long currentUserId = SecurityUtils.getUserId();
        if (currentUserId != null)
        {
            comment.setUserId(currentUserId);
        }
        if (comment.getCreateBy() == null || comment.getCreateBy().isEmpty())
        {
            // BaseEntity.createBy 用于记录评论人标识，这里优先使用用户名
            String username = SecurityUtils.getUsername();
            comment.setCreateBy(username != null ? username : String.valueOf(currentUserId));
        }
        comment.setTaskId(taskId);
        int rows = commentMapper.insert(comment);
        if (rows > 0 && kafkaTemplate != null)
        {
            Long taskOwnerId = task.getAssigneeId();
            String commentUser = comment.getCreateBy();
            Map<String, Object> event = new HashMap<>();
            event.put("taskId", taskId);
            event.put("taskTitle", task.getTitle());
            event.put("taskOwnerId", taskOwnerId);
            event.put("commentUser", commentUser);
            kafkaTemplate.send(MqTopicConstants.TASK_COMMENTED, String.valueOf(taskId), toJson(event));

            // 解析 @提及 并为每个被提及用户发送独立事件
            Set<String> rawMentions = extractMentions(comment.getContent());
            List<Long> mentionedUserIds = resolveMentionedUserIds(rawMentions);
            if (!mentionedUserIds.isEmpty())
            {
                for (Long mentionedUserId : mentionedUserIds)
                {
                    if (mentionedUserId == null || (currentUserId != null && currentUserId.equals(mentionedUserId)))
                    {
                        continue;
                    }
                    Map<String, Object> mentionEvent = new HashMap<>();
                    mentionEvent.put("taskId", taskId);
                    mentionEvent.put("taskTitle", task.getTitle());
                    mentionEvent.put("mentionedUserId", mentionedUserId);
                    mentionEvent.put("commentUser", commentUser);
                    mentionEvent.put("content", comment.getContent());
                    kafkaTemplate.send(MqTopicConstants.TASK_MENTIONED, String.valueOf(taskId), toJson(mentionEvent));
                }
            }
        }
        return rows;
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
        requireTaskWithMembership(taskId);
        return commentMapper.deleteById(commentId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateComment(Long taskId, TaskComment comment)
    {
        if (comment == null || comment.getTaskCommentId() == null)
        {
            throw new ServiceException("评论ID缺失");
        }
        TaskComment existing = commentMapper.selectById(comment.getTaskCommentId());
        if (existing == null)
        {
            throw new ServiceException("评论不存在");
        }
        if (!existing.getTaskId().equals(taskId))
        {
            throw new ServiceException("评论不属于该任务");
        }

        // 校验项目成员身份
        requireTaskWithMembership(taskId);

        Long currentUserId = SecurityUtils.getUserId();
        if (currentUserId == null || !currentUserId.equals(existing.getUserId()))
        {
            throw new ServiceException("仅创建者可以编辑评论");
        }

        existing.setContent(comment.getContent());
        existing.setUpdateBy(SecurityUtils.getUsername());
        existing.setUpdateTime(new Date());
        return commentMapper.updateById(existing);
    }

    private void enrichTask(Task task)
    {
        List<Long> watcherIds = watcherMapper.selectWatcherIdsByTaskId(task.getTaskId());
        task.setWatcherIds(watcherIds);
        task.setWatchers(watcherIds);
        task.setChildCount(taskMapper.countByParentId(task.getTaskId()));
        task.setHasBlockers(!dependencyMapper.selectDependencyTaskIds(task.getTaskId()).isEmpty());
        task.setDepth(calculateDepth(task.getParentId()));
        // Enrich label names and IDs
        List<Label> taskLabels = labelMapper.selectLabelsByTaskId(task.getTaskId());
        if (taskLabels != null)
        {
            task.setLabels(taskLabels.stream().map(Label::getName).collect(Collectors.toList()));
            task.setLabelIds(taskLabels.stream().map(Label::getLabelId).collect(Collectors.toList()));
        }
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

    @Override
    public List<Task> selectDeletedTasks(Long projectId)
    {
        return taskMapper.selectDeletedList(projectId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int restoreTask(Long taskId)
    {
        requireTaskWithMembership(taskId);
        return taskMapper.restoreById(taskId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int permanentDeleteTask(Long taskId)
    {
        requireTaskWithMembership(taskId);
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addIssueLink(Long taskId, IssueLink link)
    {
        requireTaskWithMembership(taskId);
        Task target = taskMapper.selectById(link.getTargetTaskId());
        if (target == null) throw new ServiceException("目标任务不存在");
        if (taskId.equals(link.getTargetTaskId())) throw new ServiceException("不能关联自身");

        link.setSourceTaskId(taskId);
        link.setCreateBy(String.valueOf(com.lest.common.security.utils.SecurityUtils.getUserId()));
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
        IssueLink link = issueLinkMapper.selectById(linkId);
        if (link == null)
        {
            throw new ServiceException("关联不存在");
        }
        requireTaskWithMembership(link.getSourceTaskId());
        return issueLinkMapper.deleteById(linkId);
    }

    @Override
    public List<IssueLinkType> selectIssueLinkTypes()
    {
        return issueLinkTypeMapper.selectAllActive();
    }

    @Override
    public List<Attachment> selectAttachments(Long taskId)
    {
        return attachmentMapper.selectByTaskId(taskId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int uploadAttachment(Long taskId, Attachment attachment)
    {
        requireTaskWithMembership(taskId);
        attachment.setTaskId(taskId);
        attachment.setUploadedBy(com.lest.common.security.utils.SecurityUtils.getUserId());
        Long maxVersion = attachmentMapper.getMaxVersionByFileName(taskId, attachment.getFileName());
        attachment.setVersion(maxVersion != null ? maxVersion + 1 : 1L);
        return attachmentMapper.insert(attachment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteAttachment(Long attachmentId)
    {
        Attachment attachment = attachmentMapper.selectById(attachmentId);
        if (attachment == null)
        {
            throw new ServiceException("附件不存在");
        }
        requireTaskWithMembership(attachment.getTaskId());
        Long userId = com.lest.common.security.utils.SecurityUtils.getUserId();
        return attachmentMapper.softDeleteById(attachmentId, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int restoreAttachment(Long attachmentId)
    {
        Attachment attachment = attachmentMapper.selectById(attachmentId);
        if (attachment == null)
        {
            throw new ServiceException("附件不存在");
        }
        requireTaskWithMembership(attachment.getTaskId());
        return attachmentMapper.restoreById(attachmentId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int voteTask(Long taskId)
    {
        requireTaskWithMembership(taskId);
        Long userId = com.lest.common.security.utils.SecurityUtils.getUserId();
        if (taskVoteMapper.selectByTaskIdAndUserId(taskId, userId) != null)
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
        requireTaskWithMembership(taskId);
        Long userId = com.lest.common.security.utils.SecurityUtils.getUserId();
        return taskVoteMapper.deleteByTaskIdAndUserId(taskId, userId);
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
        rule.setCreateBy(String.valueOf(com.lest.common.security.utils.SecurityUtils.getUserId()));
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

    @Override
    public int updateTaskEstimate(Long taskId, Map<String, Object> params)
    {
        Task task = requireTaskWithMembership(taskId);
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
        Task task = requireTaskWithMembership(taskId);
        Object val = params.get("remainingHours");
        if (val == null) throw new ServiceException("剩余工时不能为空");
        task.setRemainingHours(new BigDecimal(val.toString()));
        return taskMapper.updateById(task);
    }

    @Override
    public int updateTaskStoryPoints(Long taskId, Map<String, Object> params)
    {
        Task task = requireTaskWithMembership(taskId);
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
                checkProjectMembership(task.getProjectId());
                task.setIterationId(iterationId);
                count += taskMapper.updateById(task);
            }
        }
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int watchTask(Long taskId)
    {
        requireTaskWithMembership(taskId);
        Long userId = com.lest.common.security.utils.SecurityUtils.getUserId();
        if (userId == null) throw new ServiceException("无法获取当前用户");
        TaskWatcher watcher = new TaskWatcher();
        watcher.setTaskId(taskId);
        watcher.setUserId(userId);
        try { watcherMapper.insert(watcher); } catch (Exception ignored) { }
        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int unwatchTask(Long taskId)
    {
        requireTaskWithMembership(taskId);
        Long userId = com.lest.common.security.utils.SecurityUtils.getUserId();
        if (userId == null) throw new ServiceException("无法获取当前用户");
        watcherMapper.deleteByTaskIdAndUserId(taskId, userId);
        return 1;
    }

    @Override
    public boolean isWatching(Long taskId)
    {
        Long userId = com.lest.common.security.utils.SecurityUtils.getUserId();
        if (userId == null) return false;
        List<Long> ids = watcherMapper.selectWatcherIdsByTaskId(taskId);
        return ids.contains(userId);
    }

    @Override
    public List<Long> getWatchedTaskIds()
    {
        Long userId = com.lest.common.security.utils.SecurityUtils.getUserId();
        if (userId == null) return Collections.emptyList();
        return watcherMapper.selectTaskIdsByUserId(userId);
    }

    @Override
    public List<TaskChangeHistory> getChangeHistory(Long taskId)
    {
        return changeHistoryMapper.selectByTaskId(taskId);
    }

    @Override
    public List<Map<String, Object>> getWatcherUsers(Long taskId)
    {
        List<Long> watcherIds = watcherMapper.selectWatcherIdsByTaskId(taskId);
        if (watcherIds.isEmpty()) return Collections.emptyList();
        // 通过 RemoteUserService 获取用户信息（ID、用户名、昵称、头像）
        return watcherIds.stream().map(userId -> {
            Map<String, Object> user = new HashMap<>();
            user.put("userId", userId);
            user.put("userName", "");
            user.put("nickName", "");
            user.put("avatar", "");
            return user;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> cloneTask(Long taskId, Map<String, Object> params)
    {
        Task source = requireTaskWithMembership(taskId);
        Boolean includeSubtasks = params != null && Boolean.TRUE.equals(params.get("includeSubtasks"));
        Boolean includeAttachments = params != null && Boolean.TRUE.equals(params.get("includeAttachments"));
        Long targetProjectId = params != null ? ((Number) params.getOrDefault("projectId", source.getProjectId())).longValue() : source.getProjectId();
        Long targetIterationId = params != null ? ((Number) params.getOrDefault("iterationId", source.getIterationId() != null ? source.getIterationId() : 0L)).longValue() : (source.getIterationId() != null ? source.getIterationId() : 0L);
        if (targetIterationId == 0L) targetIterationId = null;
        checkProjectMembership(targetProjectId);

        Task clone = new Task();
        clone.setProjectId(targetProjectId);
        clone.setIterationId(targetIterationId);
        clone.setTitle((String) params.getOrDefault("title", source.getTitle() + " (副本)"));
        clone.setDescription(source.getDescription());
        clone.setTaskType(source.getTaskType());
        clone.setPriority(source.getPriority());
        clone.setStatus("todo");
        clone.setAssigneeId(null);
        clone.setStartTime(null);
        clone.setEstimatedHours(source.getEstimatedHours());
        clone.setDueDate(null);
        clone.setIsSubtask(0);
        clone.setRootId(null);
        clone.setStoryPoints(source.getStoryPoints());
        clone.setCreateBy(String.valueOf(com.lest.common.security.utils.SecurityUtils.getUserId()));
        taskMapper.insert(clone);

        if (includeSubtasks)
        {
            List<Task> subtasks = taskMapper.selectByParentId(taskId);
            for (Task sub : subtasks)
            {
                checkProjectMembership(sub.getProjectId());
                Task subClone = new Task();
                subClone.setProjectId(targetProjectId);
                subClone.setIterationId(targetIterationId);
                subClone.setTitle(sub.getTitle());
                subClone.setDescription(sub.getDescription());
                subClone.setTaskType(sub.getTaskType());
                subClone.setPriority(sub.getPriority());
                subClone.setStatus("todo");
                subClone.setParentId(clone.getTaskId());
                subClone.setIsSubtask(1);
                subClone.setRootId(clone.getTaskId());
                subClone.setStoryPoints(sub.getStoryPoints());
                subClone.setEstimatedHours(sub.getEstimatedHours());
                subClone.setCreateBy(String.valueOf(com.lest.common.security.utils.SecurityUtils.getUserId()));
                taskMapper.insert(subClone);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("id", clone.getTaskId());
        result.put("title", clone.getTitle());
        result.put("taskKey", "TASK-" + clone.getTaskId());
        return result;
    }

    @Override
    public List<Task> selectTasksByIterationId(Long iterationId)
    {
        return taskMapper.selectTasksByIterationId(iterationId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int moveUnfinishedTasksToIteration(Long fromIterationId, Long toIterationId)
    {
        return taskMapper.moveUnfinishedToIteration(fromIterationId, toIterationId);
    }
}
