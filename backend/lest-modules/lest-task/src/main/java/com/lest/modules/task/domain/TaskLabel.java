package com.lest.modules.task.domain;

import java.io.Serializable;

/**
 * 任务标签关联对象 task_label
 * 
 * @author yshan2028
 */
public class TaskLabel implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Long taskId;
    private Long labelId;

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public Long getLabelId() { return labelId; }
    public void setLabelId(Long labelId) { this.labelId = labelId; }
}
