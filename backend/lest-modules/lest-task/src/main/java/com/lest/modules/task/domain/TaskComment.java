package com.lest.modules.task.domain;

import com.lest.common.core.web.domain.BaseEntity;

/**
 * 任务评论对象 task_comment
 * 
 * @author yshan2028
 */
public class TaskComment extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long taskId;
    private Long userId;
    private String content;
    private Long parentId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
}
