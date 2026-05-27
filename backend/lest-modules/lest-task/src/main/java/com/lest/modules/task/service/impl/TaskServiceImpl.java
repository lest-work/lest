package com.lest.modules.task.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lest.common.base.Assert;
import com.lest.modules.task.common.ErrorCode;
import com.lest.common.base.PageResult;
import com.lest.modules.task.entity.domain.*;
import com.lest.modules.task.entity.dto.*;
import com.lest.modules.task.entity.vo.*;
import com.lest.modules.task.mapper.*;
import com.lest.common.security.util.SecurityUtils;
import com.lest.modules.task.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 任务服务实现
 *
 * @author Lest
 * @since 2026-05-26
 */
@Slf4j
@Service
public class TaskServiceImpl implements TaskService {

    private static final int MAX_DEPTH = 3;
    private static final List<String> STATUS_FLOW = List.of("todo", "in_progress", "completed");

    private final TaskMapper taskMapper;
    private final TaskWorklogMapper worklogMapper;
    private final TaskLabelMapper taskLabelMapper;
    private final TaskDependencyMapper dependencyMapper;
    private final TaskWatcherMapper watcherMapper;
    private final TaskCommitMapper commitMapper;
    private final SecurityUtils securityUtils;

    public TaskServiceImpl(TaskMapper taskMapper,
                          TaskWorklogMapper worklogMapper,
                          TaskLabelMapper taskLabelMapper,
                          TaskDependencyMapper dependencyMapper,
                          TaskWatcherMapper watcherMapper,
                          TaskCommitMapper commitMapper,
                          SecurityUtils securityUtils) {
        this.taskMapper = taskMapper;
        this.worklogMapper = worklogMapper;
        this.taskLabelMapper = taskLabelMapper;
        this.dependencyMapper = dependencyMapper;
        this.watcherMapper = watcherMapper;
        this.commitMapper = commitMapper;
        this.securityUtils = securityUtils;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(TaskDTO dto) {
        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setProjectId(dto.getProjectId());
        task.setIterationId(dto.getIterationId());
        task.setParentId(dto.getParentId());
        task.setTaskType(dto.getTaskType());
        task.setPriority(dto.getPriority());
        task.setStatus("todo");
        task.setAssigneeId(dto.getAssigneeId());
        task.setStartTime(dto.getStartTime());
        task.setEstimatedHours(dto.getEstimatedHours());
        task.setDueDate(dto.getDueDate());

        if (dto.getParentId() != null) {
            int depth = calculateDepth(dto.getParentId());
            Assert.isFalse(depth >= MAX_DEPTH, ErrorCode.TASK_DEPTH_EXCEEDED);
            task.setParentId(dto.getParentId());
        }

        if (dto.getSort() != null) {
            task.setSort(dto.getSort());
        } else {
            Integer maxSort = dto.getParentId() != null
                    ? taskMapper.getMaxSortByParentId(dto.getParentId())
                    : taskMapper.getMaxSortByProjectId(dto.getProjectId());
            task.setSort(maxSort != null ? maxSort + 1 : 0);
        }

        taskMapper.insert(task);

        if (dto.getLabelIds() != null && !dto.getLabelIds().isEmpty()) {
            saveLabels(task.getId(), dto.getLabelIds());
        }

        if (dto.getWatcherIds() != null && !dto.getWatcherIds().isEmpty()) {
            saveWatchers(task.getId(), dto.getWatcherIds());
        } else {
            Long currentUserId = securityUtils.getCurrentUserId();
            if (currentUserId != null) {
                saveWatchers(task.getId(), List.of(currentUserId));
            }
        }

        log.info("创建任务成功: taskId={}, title={}", task.getId(), dto.getTitle());
        return task.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(TaskDTO dto) {
        Task task = taskMapper.selectById(dto.getId());
        Assert.notNull(task, ErrorCode.TASK_NOT_FOUND);

        if (dto.getTitle() != null) task.setTitle(dto.getTitle());
        if (dto.getDescription() != null) task.setDescription(dto.getDescription());
        if (dto.getIterationId() != null) task.setIterationId(dto.getIterationId());
        if (dto.getTaskType() != null) task.setTaskType(dto.getTaskType());
        if (dto.getPriority() != null) task.setPriority(dto.getPriority());
        if (dto.getAssigneeId() != null) task.setAssigneeId(dto.getAssigneeId());
        if (dto.getStartTime() != null) task.setStartTime(dto.getStartTime());
        if (dto.getEstimatedHours() != null) task.setEstimatedHours(dto.getEstimatedHours());
        if (dto.getDueDate() != null) task.setDueDate(dto.getDueDate());
        if (dto.getSort() != null) task.setSort(dto.getSort());

        taskMapper.updateById(task);

        if (dto.getLabelIds() != null) {
            taskLabelMapper.deleteByTaskId(dto.getId());
            saveLabels(dto.getId(), dto.getLabelIds());
        }

        if (dto.getWatcherIds() != null) {
            watcherMapper.deleteByTaskId(dto.getId());
            saveWatchers(dto.getId(), dto.getWatcherIds());
        }

        log.info("更新任务成功: taskId={}", dto.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Task task = taskMapper.selectById(id);
        Assert.notNull(task, ErrorCode.TASK_NOT_FOUND);

        int childCount = taskMapper.countByParentId(id);
        Assert.isFalse(childCount > 0, ErrorCode.TASK_HAS_DEPENDENCIES);

        int dependencyCount = dependencyMapper.countByTaskId(id);
        Assert.isFalse(dependencyCount > 0, ErrorCode.TASK_HAS_DEPENDENCIES);

        taskMapper.deleteById(id);
        taskLabelMapper.deleteByTaskId(id);
        watcherMapper.deleteByTaskId(id);

        log.info("删除任务成功: taskId={}", id);
    }

    @Override
    public TaskVO getById(Long id) {
        Task task = taskMapper.selectById(id);
        Assert.notNull(task, ErrorCode.TASK_NOT_FOUND);
        return convertToVO(task);
    }

    @Override
    public PageResult<TaskVO> page(Long projectId, Long iterationId, Long assigneeId, String status,
                                   String priority, List<Long> labels, String keyword, Integer page, Integer size) {
        Page<Task> pageParam = new Page<>(page, size);

        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Task::getDeleted, 0);

        if (projectId != null) wrapper.eq(Task::getProjectId, projectId);
        if (iterationId != null) wrapper.eq(Task::getIterationId, iterationId);
        if (assigneeId != null) wrapper.eq(Task::getAssigneeId, assigneeId);
        if (status != null) wrapper.eq(Task::getStatus, status);
        if (priority != null) wrapper.eq(Task::getPriority, priority);
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Task::getTitle, keyword).or().like(Task::getDescription, keyword));
        }

        wrapper.orderByDesc(Task::getSort).orderByDesc(Task::getCreatedAt);

        Page<Task> result = taskMapper.selectPage(pageParam, wrapper);

        List<TaskVO> records = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.of(records, result.getTotal(), (int) result.getCurrent(), (int) result.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, String status) {
        Task task = taskMapper.selectById(id);
        Assert.notNull(task, ErrorCode.TASK_NOT_FOUND);

        if ("completed".equals(status)) {
            checkBlockers(id);
        }

        task.setStatus(status);
        if ("completed".equals(status)) {
            task.setCompletedAt(LocalDateTime.now());
        } else if ("in_progress".equals(status) && task.getStartTime() == null) {
            task.setStartTime(LocalDateTime.now());
        }

        taskMapper.updateById(task);
        log.info("更新任务状态: taskId={}, status={}", id, status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assign(Long id, AssignDTO dto) {
        Task task = taskMapper.selectById(id);
        Assert.notNull(task, ErrorCode.TASK_NOT_FOUND);

        task.setAssigneeId(dto.getAssigneeId());
        taskMapper.updateById(task);

        if (dto.getWatcherIds() != null) {
            watcherMapper.deleteByTaskId(id);
            saveWatchers(id, dto.getWatcherIds());
        }

        log.info("分配任务: taskId={}, assigneeId={}", id, dto.getAssigneeId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void claim(Long id) {
        Task task = taskMapper.selectById(id);
        Assert.notNull(task, ErrorCode.TASK_NOT_FOUND);

        Long currentUserId = securityUtils.getCurrentUserId();
        Assert.notNull(currentUserId, ErrorCode.PERMISSION_DENIED);

        task.setAssigneeId(currentUserId);
        taskMapper.updateById(task);

        log.info("认领任务: taskId={}, userId={}", id, currentUserId);
    }

    @Override
    public List<BoardVO> getBoard(Long projectId, Long iterationId) {
        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Task::getDeleted, 0);
        if (projectId != null) wrapper.eq(Task::getProjectId, projectId);
        if (iterationId != null) wrapper.eq(Task::getIterationId, iterationId);
        wrapper.isNull(Task::getParentId);
        wrapper.orderByDesc(Task::getSort);

        List<Task> tasks = taskMapper.selectList(wrapper);

        Map<String, List<TaskVO>> grouped = tasks.stream()
                .map(this::convertToVO)
                .collect(Collectors.groupingBy(TaskVO::getStatus));

        return STATUS_FLOW.stream()
                .map(status -> {
                    List<TaskVO> columnTasks = grouped.getOrDefault(status, List.of());
                    return BoardVO.builder()
                            .column(status)
                            .tasks(columnTasks)
                            .totalCount(columnTasks.size())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void move(Long id, MoveDTO dto) {
        Task task = taskMapper.selectById(id);
        Assert.notNull(task, ErrorCode.TASK_NOT_FOUND);

        if ("completed".equals(dto.getTargetColumn())) {
            checkBlockers(id);
        }

        task.setStatus(dto.getTargetColumn());
        if (dto.getTargetPosition() != null) {
            task.setSort(dto.getTargetPosition());
        }

        taskMapper.updateById(task);
        log.info("移动任务: taskId={}, to={}", id, dto.getTargetColumn());
    }

    @Override
    public List<GanttVO> getGantt(Long projectId, Long iterationId, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Task::getDeleted, 0);
        if (projectId != null) wrapper.eq(Task::getProjectId, projectId);
        if (iterationId != null) wrapper.eq(Task::getIterationId, iterationId);
        if (startDate != null) wrapper.ge(Task::getDueDate, startDate);
        if (endDate != null) wrapper.le(Task::getDueDate, endDate);

        List<Task> tasks = taskMapper.selectList(wrapper);

        return buildGanttTree(tasks, null, 0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createSubtask(Long parentId, TaskDTO dto) {
        Task parent = taskMapper.selectById(parentId);
        Assert.notNull(parent, ErrorCode.TASK_PARENT_NOT_FOUND);

        int depth = calculateDepth(parentId);
        Assert.isFalse(depth >= MAX_DEPTH, ErrorCode.TASK_DEPTH_EXCEEDED);

        dto.setParentId(parentId);
        return create(dto);
    }

    @Override
    public List<TaskVO> getSubtasks(Long parentId) {
        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Task::getDeleted, 0);
        wrapper.eq(Task::getParentId, parentId);
        wrapper.orderByDesc(Task::getSort);

        return taskMapper.selectList(wrapper).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addDependency(Long id, DependencyDTO dto) {
        Task task = taskMapper.selectById(id);
        Assert.notNull(task, ErrorCode.TASK_NOT_FOUND);

        Task depTask = taskMapper.selectById(dto.getDependencyTaskId());
        Assert.notNull(depTask, ErrorCode.TASK_NOT_FOUND);

        Assert.isFalse(id.equals(dto.getDependencyTaskId()), ErrorCode.TASK_CIRCULAR_DEPENDENCY);

        Assert.isFalse(hasCircularDependency(id, dto.getDependencyTaskId()), ErrorCode.TASK_CIRCULAR_DEPENDENCY);

        TaskDependency dependency = new TaskDependency();
        dependency.setTaskId(id);
        dependency.setDependencyTaskId(dto.getDependencyTaskId());
        dependency.setType(dto.getType());
        dependencyMapper.insert(dependency);

        log.info("添加任务依赖: taskId={}, depTaskId={}", id, dto.getDependencyTaskId());
    }

    @Override
    public List<TaskDependencyVO> getDependencies(Long id) {
        List<Long> depIds = dependencyMapper.selectDependencyTaskIds(id);
        List<Task> depTasks = depIds.isEmpty() ? List.of() : taskMapper.selectList(
                new LambdaQueryWrapper<Task>().in(Task::getId, depIds));

        Map<Long, Task> taskMap = depTasks.stream()
                .collect(Collectors.toMap(Task::getId, t -> t));

        return depIds.stream().map(depId -> {
            Task depTask = taskMap.get(depId);
            return TaskDependencyVO.builder()
                    .taskId(id)
                    .dependencyTaskId(depId)
                    .dependencyTaskTitle(depTask != null ? depTask.getTitle() : null)
                    .dependencyTaskStatus(depTask != null ? depTask.getStatus() : null)
                    .type("blocker")
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDependency(Long id, Long dependencyTaskId) {
        dependencyMapper.delete(new LambdaQueryWrapper<TaskDependency>()
                .eq(TaskDependency::getTaskId, id)
                .eq(TaskDependency::getDependencyTaskId, dependencyTaskId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addWorklog(Long id, WorklogDTO dto) {
        Task task = taskMapper.selectById(id);
        Assert.notNull(task, ErrorCode.TASK_NOT_FOUND);

        Assert.notNull(dto.getHours(), ErrorCode.WORKLOG_HOURS_INVALID);
        Assert.isTrue(dto.getHours().compareTo(BigDecimal.ZERO) > 0, ErrorCode.WORKLOG_HOURS_INVALID);

        Long currentUserId = securityUtils.getCurrentUserId();
        Assert.notNull(currentUserId, ErrorCode.PERMISSION_DENIED);

        TaskWorklog worklog = new TaskWorklog();
        worklog.setTaskId(id);
        worklog.setUserId(currentUserId);
        worklog.setHours(dto.getHours());
        worklog.setDescription(dto.getDescription());
        worklog.setWorkDate(dto.getWorkDate());

        worklogMapper.insert(worklog);

        BigDecimal totalHours = task.getActualHours() != null ? task.getActualHours() : BigDecimal.ZERO;
        task.setActualHours(totalHours.add(dto.getHours()));
        taskMapper.updateById(task);

        log.info("添加工时记录: worklogId={}, taskId={}, hours={}", worklog.getId(), id, dto.getHours());
        return worklog.getId();
    }

    @Override
    public PageResult<WorklogVO> getWorklogs(Long id, Integer page, Integer size) {
        Page<TaskWorklog> pageParam = new Page<>(page, size);

        LambdaQueryWrapper<TaskWorklog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TaskWorklog::getTaskId, id);
        wrapper.orderByDesc(TaskWorklog::getWorkDate);

        Page<TaskWorklog> result = worklogMapper.selectPage(pageParam, wrapper);

        List<WorklogVO> records = result.getRecords().stream()
                .map(w -> WorklogVO.builder()
                        .id(w.getId())
                        .taskId(w.getTaskId())
                        .userId(w.getUserId())
                        .hours(w.getHours())
                        .description(w.getDescription())
                        .workDate(w.getWorkDate())
                        .createdAt(w.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return PageResult.of(records, result.getTotal(), (int) result.getCurrent(), (int) result.getSize());
    }

    @Override
    public List<CommitVO> getCommits(Long id) {
        List<TaskCommit> commits = commitMapper.selectCommitsByTaskId(id);
        return commits.stream()
                .map(c -> CommitVO.builder()
                        .id(c.getId())
                        .taskId(c.getTaskId())
                        .commitHash(c.getCommitHash())
                        .shortHash(c.getCommitHash() != null && c.getCommitHash().length() > 7
                                ? c.getCommitHash().substring(0, 7) : c.getCommitHash())
                        .commitMessage(c.getCommitMessage())
                        .author(c.getAuthor())
                        .commitTime(c.getCommitTime())
                        .repoId(c.getRepoId())
                        .type(c.getType())
                        .source(c.getSource())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<MergeRequestVO> getMergeRequests(Long id) {
        List<TaskCommit> mrs = commitMapper.selectMRsByTaskId(id);
        return mrs.stream()
                .map(m -> MergeRequestVO.builder()
                        .id(m.getId())
                        .taskId(m.getTaskId())
                        .mrId(m.getMrId())
                        .mrTitle(m.getMrTitle())
                        .mrStatus(m.getMrStatus())
                        .repoId(m.getRepoId())
                        .source(m.getSource())
                        .createdAt(m.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addCommit(Long id, CommitDTO dto) {
        Task task = taskMapper.selectById(id);
        Assert.notNull(task, ErrorCode.TASK_NOT_FOUND);

        TaskCommit commit = new TaskCommit();
        commit.setTaskId(id);
        commit.setCommitHash(dto.getCommitHash());
        commit.setCommitMessage(dto.getCommitMessage());
        commit.setAuthor(dto.getAuthor());
        commit.setCommitTime(dto.getCommitTime());
        commit.setRepoId(dto.getRepoId());
        commit.setType("commit");
        commit.setSource("manual");

        commitMapper.insert(commit);
        log.info("手动关联提交: taskId={}, commitHash={}", id, dto.getCommitHash());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addMergeRequest(Long id, MergeRequestDTO dto) {
        Task task = taskMapper.selectById(id);
        Assert.notNull(task, ErrorCode.TASK_NOT_FOUND);

        TaskCommit mr = new TaskCommit();
        mr.setTaskId(id);
        mr.setMrId(dto.getMrId());
        mr.setMrTitle(dto.getMrTitle());
        mr.setMrStatus(dto.getMrStatus());
        mr.setRepoId(dto.getRepoId());
        mr.setType("mr");
        mr.setSource("manual");

        commitMapper.insert(mr);
        log.info("手动关联MR: taskId={}, mrId={}", id, dto.getMrId());
    }

    private void checkBlockers(Long taskId) {
        List<Long> blockers = dependencyMapper.selectDependencyTaskIds(taskId);
        if (!blockers.isEmpty()) {
            List<Task> blockerTasks = taskMapper.selectList(
                    new LambdaQueryWrapper<Task>().in(Task::getId, blockers));
            boolean hasIncomplete = blockerTasks.stream()
                    .anyMatch(t -> !"completed".equals(t.getStatus()));
            Assert.isFalse(hasIncomplete, ErrorCode.TASK_BLOCKED_BY_OTHERS);
        }
    }

    private boolean hasCircularDependency(Long taskId, Long newDepId) {
        Set<Long> visited = new HashSet<>();
        Queue<Long> queue = new LinkedList<>();
        queue.add(newDepId);

        while (!queue.isEmpty()) {
            Long current = queue.poll();
            if (current.equals(taskId)) {
                return true;
            }
            if (visited.contains(current)) {
                continue;
            }
            visited.add(current);

            List<Long> deps = dependencyMapper.selectDependencyTaskIds(current);
            queue.addAll(deps);
        }
        return false;
    }

    private int calculateDepth(Long parentId) {
        int depth = 0;
        Long currentId = parentId;
        while (currentId != null && depth < MAX_DEPTH) {
            Task parent = taskMapper.selectById(currentId);
            if (parent == null) break;
            currentId = parent.getParentId();
            depth++;
        }
        return depth;
    }

    private void saveLabels(Long taskId, List<Long> labelIds) {
        List<TaskLabel> labels = labelIds.stream().map(labelId -> {
            TaskLabel tl = new TaskLabel();
            tl.setTaskId(taskId);
            tl.setLabelId(labelId);
            return tl;
        }).collect(Collectors.toList());
        for (TaskLabel label : labels) {
            taskLabelMapper.insert(label);
        }
    }

    private void saveWatchers(Long taskId, List<Long> userIds) {
        List<TaskWatcher> watchers = userIds.stream().map(userId -> {
            TaskWatcher tw = new TaskWatcher();
            tw.setTaskId(taskId);
            tw.setUserId(userId);
            return tw;
        }).collect(Collectors.toList());
        for (TaskWatcher watcher : watchers) {
            watcherMapper.insert(watcher);
        }
    }

    private TaskVO convertToVO(Task task) {
        List<Long> labelIds = taskLabelMapper.selectLabelIdsByTaskId(task.getId());
        List<Long> watcherIds = watcherMapper.selectWatcherIdsByTaskId(task.getId());
        int childCount = taskMapper.countByParentId(task.getId());
        boolean hasBlockers = !dependencyMapper.selectDependencyTaskIds(task.getId()).isEmpty();
        int depth = calculateDepth(task.getParentId());

        return TaskVO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .projectId(task.getProjectId())
                .iterationId(task.getIterationId())
                .parentId(task.getParentId())
                .taskType(task.getTaskType())
                .priority(task.getPriority())
                .status(task.getStatus())
                .assigneeId(task.getAssigneeId())
                .startTime(task.getStartTime())
                .completedAt(task.getCompletedAt())
                .estimatedHours(task.getEstimatedHours())
                .actualHours(task.getActualHours())
                .dueDate(task.getDueDate())
                .sort(task.getSort())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .watcherIds(watcherIds)
                .childCount(childCount)
                .depth(depth)
                .hasBlockers(hasBlockers)
                .build();
    }

    private List<GanttVO> buildGanttTree(List<Task> allTasks, Long parentId, int depth) {
        return allTasks.stream()
                .filter(t -> Objects.equals(t.getParentId(), parentId))
                .map(task -> {
                    List<GanttVO> children = buildGanttTree(allTasks, task.getId(), depth + 1);
                    int progress = calculateProgress(task, children);

                    return GanttVO.builder()
                            .id(task.getId())
                            .title(task.getTitle())
                            .taskType(task.getTaskType())
                            .priority(task.getPriority())
                            .status(task.getStatus())
                            .assigneeId(task.getAssigneeId())
                            .startDate(task.getStartTime() != null ? task.getStartTime().toLocalDate() : null)
                            .endDate(task.getDueDate())
                            .estimatedHours(task.getEstimatedHours())
                            .actualHours(task.getActualHours())
                            .progress(progress)
                            .parentId(task.getParentId())
                            .depth(depth)
                            .hasBlockers(!dependencyMapper.selectDependencyTaskIds(task.getId()).isEmpty())
                            .children(children.isEmpty() ? null : children)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private int calculateProgress(Task task, List<GanttVO> children) {
        if (children == null || children.isEmpty()) {
            return "completed".equals(task.getStatus()) ? 100 : 0;
        }
        int totalProgress = children.stream()
                .mapToInt(c -> c.getProgress() != null ? c.getProgress() : 0)
                .sum();
        return totalProgress / children.size();
    }
}
