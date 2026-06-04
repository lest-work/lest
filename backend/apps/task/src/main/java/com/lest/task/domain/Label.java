package com.lest.task.domain;

import com.lest.common.core.web.domain.BaseEntity;
import java.io.Serializable;

/**
 * 标签对象 label
 *
 * @author yshan2028
 */
public class Label extends BaseEntity implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 标签ID */
    private Long labelId;

    /** 项目ID */
    private Long projectId;

    /** 标签名称 */
    private String name;

    /** 标签颜色 */
    private String color;

    public Long getLabelId()
    {
        return labelId;
    }

    public void setLabelId(Long labelId)
    {
        this.labelId = labelId;
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

    public String getColor()
    {
        return color;
    }

    public void setColor(String color)
    {
        this.color = color;
    }
}
