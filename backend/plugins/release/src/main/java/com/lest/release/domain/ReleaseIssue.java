package com.lest.release.domain;

import java.util.Date;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.lest.common.core.web.domain.BaseEntity;

/**
 * 发布关联问题对象 release_issue
 *
 * @author yshan2028
 */
public class ReleaseIssue extends BaseEntity implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 关联ID */
    private Long releaseIssueId;

    /** 发布计划ID */
    private Long releasePlanId;

    /** 问题ID */
    private Long issueId;

    /** 任务ID */
    private Long taskId;

    /** 分类 */
    private Integer category;

    /** 备注 */
    private String notes;

    /** 添加用户ID */
    private Long addedBy;

    /** 添加时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date addedAt;

    /** 发布计划名称（非数据库字段） */
    private String releaseName;

    /** 添加人名称（非数据库字段） */
    private String addedByName;

    /** 逻辑删除标记 */
    private Boolean deleted;

    public Long getReleaseIssueId()
    {
        return releaseIssueId;
    }

    public void setReleaseIssueId(Long releaseIssueId)
    {
        this.releaseIssueId = releaseIssueId;
    }

    public Long getReleasePlanId()
    {
        return releasePlanId;
    }

    public void setReleasePlanId(Long releasePlanId)
    {
        this.releasePlanId = releasePlanId;
    }

    public Long getIssueId()
    {
        return issueId;
    }

    public void setIssueId(Long issueId)
    {
        this.issueId = issueId;
    }

    public Long getTaskId()
    {
        return taskId;
    }

    public void setTaskId(Long taskId)
    {
        this.taskId = taskId;
    }

    public Integer getCategory()
    {
        return category;
    }

    public void setCategory(Integer category)
    {
        this.category = category;
    }

    public String getNotes()
    {
        return notes;
    }

    public void setNotes(String notes)
    {
        this.notes = notes;
    }

    public Long getAddedBy()
    {
        return addedBy;
    }

    public void setAddedBy(Long addedBy)
    {
        this.addedBy = addedBy;
    }

    public Date getAddedAt()
    {
        return addedAt;
    }

    public void setAddedAt(Date addedAt)
    {
        this.addedAt = addedAt;
    }

    public String getReleaseName()
    {
        return releaseName;
    }

    public void setReleaseName(String releaseName)
    {
        this.releaseName = releaseName;
    }

    public String getAddedByName()
    {
        return addedByName;
    }

    public void setAddedByName(String addedByName)
    {
        this.addedByName = addedByName;
    }

    public Boolean getDeleted()
    {
        return deleted;
    }

    public void setDeleted(Boolean deleted)
    {
        this.deleted = deleted;
    }
}
