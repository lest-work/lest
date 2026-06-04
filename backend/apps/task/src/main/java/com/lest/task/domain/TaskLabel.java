package com.lest.task.domain;

import java.io.Serializable;

/**
 * 任务-标签关联对象 task_label（多对多关联表）
 *
 * @author yshan2028
 */
public class TaskLabel implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 任务ID */
    private Long taskId;

    /** 标签ID */
    private Long labelId;

    public Long getTaskId()
    {
        return taskId;
    }

    public void setTaskId(Long taskId)
    {
        this.taskId = taskId;
    }

    public Long getLabelId()
    {
        return labelId;
    }

    public void setLabelId(Long labelId)
    {
        this.labelId = labelId;
    }
}
