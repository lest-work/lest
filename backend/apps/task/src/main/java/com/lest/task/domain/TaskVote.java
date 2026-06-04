package com.lest.task.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lest.common.core.web.domain.BaseEntity;
import java.io.Serializable;
import java.util.Date;

/**
 * 任务投票对象 task_vote
 *
 * @author yshan2028
 */
public class TaskVote extends BaseEntity implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 投票ID */
    private Long voteId;

    /** 任务ID */
    private Long taskId;

    /** 用户ID */
    private Long userId;

    /** 投票时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date votedAt;

    public Long getVoteId()
    {
        return voteId;
    }

    public void setVoteId(Long voteId)
    {
        this.voteId = voteId;
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

    public Date getVotedAt()
    {
        return votedAt;
    }

    public void setVotedAt(Date votedAt)
    {
        this.votedAt = votedAt;
    }
}
