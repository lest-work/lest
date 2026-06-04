package com.lest.task.domain;

import java.io.Serializable;

/**
 * 任务关联类型对象 task_issue_link_type
 *
 * @author yshan2028
 */
public class IssueLinkType implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 类型ID */
    private Long typeId;

    /** 类型名称 */
    private String name;

    /** 正向描述 */
    private String inward;

    /** 反向描述 */
    private String outward;

    /** 颜色 */
    private String color;

    public Long getTypeId()
    {
        return typeId;
    }

    public void setTypeId(Long typeId)
    {
        this.typeId = typeId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getInward()
    {
        return inward;
    }

    public void setInward(String inward)
    {
        this.inward = inward;
    }

    public String getOutward()
    {
        return outward;
    }

    public void setOutward(String outward)
    {
        this.outward = outward;
    }

    public String getColor()
    {
        return color;
    }

    public void setColor(String color)
    {
        this.color = color;
    }
}
