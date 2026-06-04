package com.lest.task.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import com.lest.task.domain.Task;
import com.lest.task.domain.TaskChangeHistory;
import com.lest.task.domain.TaskComment;
import com.lest.task.domain.TaskCommit;
import com.lest.task.domain.TaskDependency;
import com.lest.task.domain.TaskWorklog;
import com.lest.task.domain.IssueLink;
import com.lest.task.domain.IssueLinkType;
import com.lest.task.domain.Attachment;
import com.lest.task.domain.TaskVote;
import com.lest.task.domain.AutomationRule;
import com.lest.task.domain.AutomationExecutionLog;

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

    public int deleteTaskById(Long taskId, Long deletedBy);

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

    public int addComment(Long taskId, TaskComment comment);

    public List<TaskComment> selectComments(Long taskId);

    public int deleteComment(Long taskId, Long commentId);

    public int updateComment(Long taskId, TaskComment comment);

    public List<TaskCommit> selectCommits(Long taskId);

    public List<TaskCommit> selectMergeRequests(Long taskId);

    public int addCommit(Long taskId, TaskCommit commit);

    public int addMergeRequest(Long taskId, TaskCommit mr);

    public List<Task> selectDeletedTasks(Long projectId);

    public int restoreTask(Long taskId);

    public int permanentDeleteTask(Long taskId);

    public int addIssueLink(Long taskId, IssueLink link);

    public List<IssueLink> selectIssueLinks(Long taskId);

    public int removeIssueLink(Long linkId);

    public List<IssueLinkType> selectIssueLinkTypes();

    public List<Attachment> selectAttachments(Long taskId);

    public int uploadAttachment(Long taskId, Attachment attachment);

    public int deleteAttachment(Long attachmentId);

    public int restoreAttachment(Long attachmentId);

    public int voteTask(Long taskId);

    public int unvoteTask(Long taskId);

    public int getVoteCount(Long taskId);

    public List<TaskVote> selectVoters(Long taskId);

    public List<AutomationRule> selectAutomationRules(Long projectId);

    public AutomationRule selectAutomationRuleById(Long ruleId);

    public int createAutomationRule(AutomationRule rule);

    public int updateAutomationRule(AutomationRule rule);

    public int deleteAutomationRule(Long ruleId);

    public int toggleAutomationRule(Long ruleId, Integer isEnabled);

    public List<AutomationExecutionLog> selectAutomationLogs(Long ruleId);

    public List<AutomationExecutionLog> selectAutomationLogsByTask(Long taskId);

    // Time Tracking
    public int updateTaskEstimate(Long taskId, Map<String, Object> params);

    public int updateTaskRemaining(Long taskId, Map<String, Object> params);

    public int updateTaskStoryPoints(Long taskId, Map<String, Object> params);

    public int batchMoveToIteration(Map<String, Object> params);

    // Watch
    public int watchTask(Long taskId);

    public int unwatchTask(Long taskId);

    public boolean isWatching(Long taskId);

    public List<Long> getWatchedTaskIds();

    public List<TaskChangeHistory> getChangeHistory(Long taskId);

    public List<Map<String, Object>> getWatcherUsers(Long taskId);

    public Map<String, Object> cloneTask(Long taskId, Map<String, Object> params);

    /**
     * 查询指定迭代下的所有任务（供 lest-project 通过 Feign 调用）
     */
    public List<Task> selectTasksByIterationId(Long iterationId);

    /**
     * 将源迭代中所有未完成的任务移入目标迭代（迭代完成时调用）
     */
    public int moveUnfinishedTasksToIteration(Long fromIterationId, Long toIterationId);
}
