package com.lest.project.domain;

import java.io.Serializable;

/**
 * 里程碑与迭代关联对象 milestone_iteration
 * 
 * @author yshan2028
 */
public class MilestoneIteration implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 里程碑ID */
    private Long milestoneId;

    /** 迭代ID */
    private Long iterationId;

    public Long getMilestoneId()
    {
        return milestoneId;
    }

    public void setMilestoneId(Long milestoneId)
    {
        this.milestoneId = milestoneId;
    }

    public Long getIterationId()
    {
        return iterationId;
    }

    public void setIterationId(Long iterationId)
    {
        this.iterationId = iterationId;
    }
}
