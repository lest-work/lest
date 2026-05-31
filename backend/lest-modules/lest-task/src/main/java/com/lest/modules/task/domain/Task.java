package com.lest.modules.task.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.lest.common.core.web.domain.BaseEntity;

/**
 * 任务对象 task
 *
 * @author yshan2028
 */
public class Task extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 任务ID */
    private Long taskId;

    /** 任务标题 */
    private String title;

    /** 任务描述 */
    private String description;

    /** 所属项目ID */
    private Long projectId;

    /** 所属迭代ID */
    private Long iterationId;

    /** 父任务ID */
    private Long parentId;

    /** 类型：story/task/bug/improvement */
    private String taskType;

    /** 优先级：p0/p1/p2/p3 */
    private String priority;

    /** 状态：todo/in_progress/completed */
    private String status;

    /** 负责人ID */
    private Long assigneeId;

    /** 负责人名称（非数据库字段） */
    private String assigneeName;

    /** 开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /** 完成时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date completedAt;

    /** 预估工时 */
    private BigDecimal estimatedHours;

    /** 实际工时 */
    private BigDecimal actualHours;

    /** 截止日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dueDate;

    /** 排序权重 */
    private Integer sort;

    /** 逻辑删除标记 */
    private Integer deleted;

    /** 标签ID列表（非数据库字段） */
    private List<Long> labelIds;

    /** 关注者ID列表（非数据库字段） */
    private List<Long> watcherIds;

    /** 子任务数量（非数据库字段） */
    private Integer childCount;

    /** 嵌套深度（非数据库字段） */
    private Integer depth;

    /** 是否有阻塞（非数据库字段） */
    private Boolean hasBlockers;

    public Long getTaskId()
    {
        return taskId;
    }

    public void setTaskId(Long taskId)
    {
        this.taskId = taskId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public Long getProjectId()
    {
        return projectId;
    }

    public void setProjectId(Long projectId)
    {
        this.projectId = projectId;
    }

    public Long getIterationId()
    {
        return iterationId;
    }

    public void setIterationId(Long iterationId)
    {
        this.iterationId = iterationId;
    }

    public Long getParentId()
    {
        return parentId;
    }

    public void setParentId(Long parentId)
    {
        this.parentId = parentId;
    }

    public String getTaskType()
    {
        return taskType;
    }

    public void setTaskType(String taskType)
    {
        this.taskType = taskType;
    }

    public String getPriority()
    {
        return priority;
    }

    public void setPriority(String priority)
    {
        this.priority = priority;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public Long getAssigneeId()
    {
        return assigneeId;
    }

    public void setAssigneeId(Long assigneeId)
    {
        this.assigneeId = assigneeId;
    }

    public String getAssigneeName()
    {
        return assigneeName;
    }

    public void setAssigneeName(String assigneeName)
    {
        this.assigneeName = assigneeName;
    }

    public Date getStartTime()
    {
        return startTime;
    }

    public void setStartTime(Date startTime)
    {
        this.startTime = startTime;
    }

    public Date getCompletedAt()
    {
        return completedAt;
    }

    public void setCompletedAt(Date completedAt)
    {
        this.completedAt = completedAt;
    }

    public BigDecimal getEstimatedHours()
    {
        return estimatedHours;
    }

    public void setEstimatedHours(BigDecimal estimatedHours)
    {
        this.estimatedHours = estimatedHours;
    }

    public BigDecimal getActualHours()
    {
        return actualHours;
    }

    public void setActualHours(BigDecimal actualHours)
    {
        this.actualHours = actualHours;
    }

    public Date getDueDate()
    {
        return dueDate;
    }

    public void setDueDate(Date dueDate)
    {
        this.dueDate = dueDate;
    }

    public Integer getSort()
    {
        return sort;
    }

    public void setSort(Integer sort)
    {
        this.sort = sort;
    }

    public Integer getDeleted()
    {
        return deleted;
    }

    public void setDeleted(Integer deleted)
    {
        this.deleted = deleted;
    }

    public List<Long> getLabelIds()
    {
        return labelIds;
    }

    public void setLabelIds(List<Long> labelIds)
    {
        this.labelIds = labelIds;
    }

    public List<Long> getWatcherIds()
    {
        return watcherIds;
    }

    public void setWatcherIds(List<Long> watcherIds)
    {
        this.watcherIds = watcherIds;
    }

    public Integer getChildCount()
    {
        return childCount;
    }

    public void setChildCount(Integer childCount)
    {
        this.childCount = childCount;
    }

    public Integer getDepth()
    {
        return depth;
    }

    public void setDepth(Integer depth)
    {
        this.depth = depth;
    }

    public Boolean getHasBlockers()
    {
        return hasBlockers;
    }

    public void setHasBlockers(Boolean hasBlockers)
    {
        this.hasBlockers = hasBlockers;
    }
}
