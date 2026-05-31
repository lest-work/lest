package com.lest.modules.project.domain;

import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.lest.common.core.web.domain.BaseEntity;

/**
 * 里程碑对象 milestone
 *
 * @author yshan2028
 */
public class Milestone extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 里程碑ID */
    private Long milestoneId;

    /** 所属项目ID */
    private Long projectId;

    /** 里程碑名称 */
    private String name;

    /** 里程碑描述 */
    private String description;

    /** 目标日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date targetDate;

    /** 关联迭代列表（非数据库字段） */
    private List<Iteration> iterations;

    public Long getMilestoneId()
    {
        return milestoneId;
    }

    public void setMilestoneId(Long milestoneId)
    {
        this.milestoneId = milestoneId;
    }

    public Long getProjectId()
    {
        return projectId;
    }

    public void setProjectId(Long projectId)
    {
        this.projectId = projectId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public Date getTargetDate()
    {
        return targetDate;
    }

    public void setTargetDate(Date targetDate)
    {
        this.targetDate = targetDate;
    }

    public List<Iteration> getIterations()
    {
        return iterations;
    }

    public void setIterations(List<Iteration> iterations)
    {
        this.iterations = iterations;
    }
}
