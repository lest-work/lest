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

    /** 评论ID */
    private Long taskCommentId;

    /** 任务ID */
    private Long taskId;

    /** 评论人用户ID */
    private Long userId;

    /** 评论内容 */
    private String content;

    /** 父评论ID（回复） */
    private Long parentId;

    public Long getTaskCommentId()
    {
        return taskCommentId;
    }

    public void setTaskCommentId(Long taskCommentId)
    {
        this.taskCommentId = taskCommentId;
    }

    public Long getTaskId()
    {
        return taskId;
    }

    public void setTaskId(Long taskId)
    {
        this.taskId = taskId;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public Long getParentId()
    {
        return parentId;
    }

    public void setParentId(Long parentId)
    {
        this.parentId = parentId;
    }
}
