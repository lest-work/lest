package com.lest.project.domain;

import java.io.Serializable;

/**
 * 项目优先级对象 project_priority
 *
 * @author yshan2028
 */
public class ProjectPriority implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 优先级ID */
    private Long priorityId;

    /** 优先级名称 */
    private String name;

    /** 颜色 */
    private String color;

    /** 排序 */
    private Integer sortOrder;

    /** 描述 */
    private String description;

    public Long getPriorityId()
    {
        return priorityId;
    }

    public void setPriorityId(Long priorityId)
    {
        this.priorityId = priorityId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getColor()
    {
        return color;
    }

    public void setColor(String color)
    {
        this.color = color;
    }

    public Integer getSortOrder()
    {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder)
    {
        this.sortOrder = sortOrder;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }
}
