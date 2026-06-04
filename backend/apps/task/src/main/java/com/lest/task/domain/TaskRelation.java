package com.lest.task.domain;

import java.io.Serializable;

/**
 * 任务关系对象 task_relation
 *
 * @author yshan2028
 */
public class TaskRelation implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 关系ID */
    private Long relationId;

    /** 任务ID */
    private Long taskId;

    /** 关联任务ID */
    private Long relatedTaskId;

    /** 关系类型 */
    private String relationType;

    public Long getRelationId()
    {
        return relationId;
    }

    public void setRelationId(Long relationId)
    {
        this.relationId = relationId;
    }

    public Long getTaskId()
    {
        return taskId;
    }

    public void setTaskId(Long taskId)
    {
        this.taskId = taskId;
    }

    public Long getRelatedTaskId()
    {
        return relatedTaskId;
    }

    public void setRelatedTaskId(Long relatedTaskId)
    {
        this.relatedTaskId = relatedTaskId;
    }

    public String getRelationType()
    {
        return relationType;
    }

    public void setRelationType(String relationType)
    {
        this.relationType = relationType;
    }
}
