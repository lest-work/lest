package com.lest.modules.task.domain;

import java.io.Serializable;

/**
 * 任务关注者对象 task_watcher
 * 
 * @author yshan2028
 */
public class TaskWatcher implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Long taskId;
    private Long userId;

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
