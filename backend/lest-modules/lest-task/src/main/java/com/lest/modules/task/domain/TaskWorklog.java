package com.lest.modules.task.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.lest.common.core.web.domain.BaseEntity;

/**
 * 工时记录对象 task_worklog
 *
 * @author yshan2028
 */
public class TaskWorklog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 工时记录ID */
    private Long taskWorklogId;

    /** 任务ID */
    private Long taskId;

    /** 记录人用户ID */
    private Long userId;

    /** 工时（小时） */
    private BigDecimal hours;

    /** 工作日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date workDate;

    /** 工作内容描述 */
    private String description;

    public Long getTaskWorklogId()
    {
        return taskWorklogId;
    }

    public void setTaskWorklogId(Long taskWorklogId)
    {
        this.taskWorklogId = taskWorklogId;
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

    public BigDecimal getHours()
    {
        return hours;
    }

    public void setHours(BigDecimal hours)
    {
        this.hours = hours;
    }

    public Date getWorkDate()
    {
        return workDate;
    }

    public void setWorkDate(Date workDate)
    {
        this.workDate = workDate;
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
