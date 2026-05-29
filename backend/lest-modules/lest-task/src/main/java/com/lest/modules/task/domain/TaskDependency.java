package com.lest.modules.task.domain;

import java.io.Serializable;

/**
 * 任务依赖对象 task_dependency
 * 
 * @author yshan2028
 */
public class TaskDependency implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Long taskId;
    private Long dependencyTaskId;
    private String type;

    /** 依赖任务标题（非数据库字段） */
    private String dependencyTaskTitle;
    /** 依赖任务状态（非数据库字段） */
    private String dependencyTaskStatus;

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public Long getDependencyTaskId() { return dependencyTaskId; }
    public void setDependencyTaskId(Long dependencyTaskId) { this.dependencyTaskId = dependencyTaskId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getDependencyTaskTitle() { return dependencyTaskTitle; }
    public void setDependencyTaskTitle(String dependencyTaskTitle) { this.dependencyTaskTitle = dependencyTaskTitle; }
    public String getDependencyTaskStatus() { return dependencyTaskStatus; }
    public void setDependencyTaskStatus(String dependencyTaskStatus) { this.dependencyTaskStatus = dependencyTaskStatus; }
}
