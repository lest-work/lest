package com.lest.task.domain;

import com.lest.common.core.web.domain.BaseEntity;
import java.io.Serializable;

/**
 * 任务关联对象 task_issue_link
 *
 * @author yshan2028
 */
public class IssueLink extends BaseEntity implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 关联ID */
    private Long linkId;

    /** 任务ID */
    private Long taskId;

    /** 关联的任务ID */
    private Long linkedTaskId;

    /** 目标任务ID */
    private Long targetTaskId;

    /** 关联类型 */
    private String linkType;

    /** 源任务ID */
    private Long sourceTaskId;

    public Long getLinkId()
    {
        return linkId;
    }

    public void setLinkId(Long linkId)
    {
        this.linkId = linkId;
    }

    public Long getTaskId()
    {
        return taskId;
    }

    public void setTaskId(Long taskId)
    {
        this.taskId = taskId;
    }

    public Long getLinkedTaskId()
    {
        return linkedTaskId;
    }

    public void setLinkedTaskId(Long linkedTaskId)
    {
        this.linkedTaskId = linkedTaskId;
    }

    public Long getTargetTaskId()
    {
        return targetTaskId;
    }

    public void setTargetTaskId(Long targetTaskId)
    {
        this.targetTaskId = targetTaskId;
    }

    public String getLinkType()
    {
        return linkType;
    }

    public void setLinkType(String linkType)
    {
        this.linkType = linkType;
    }

    public Long getSourceTaskId()
    {
        return sourceTaskId;
    }

    public void setSourceTaskId(Long sourceTaskId)
    {
        this.sourceTaskId = sourceTaskId;
    }
}
