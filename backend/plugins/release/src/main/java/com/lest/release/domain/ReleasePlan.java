package com.lest.release.domain;

import java.util.Date;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.lest.common.core.web.domain.BaseEntity;

/**
 * 发布计划对象 release_plan
 *
 * @author yshan2028
 */
public class ReleasePlan extends BaseEntity implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 发布计划ID */
    private Long releasePlanId;

    /** 项目ID */
    private Long projectId;

    /** 计划名称 */
    private String name;

    /** 描述 */
    private String description;

    /** 发布日期 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date releaseDate;

    /** 前端兼容字段别名（映射到 releaseDate） */
    @com.fasterxml.jackson.annotation.JsonProperty("plannedDate")
    public String getPlannedDate()
    {
        if (releaseDate == null)
        {
            return null;
        }
        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(releaseDate);
    }

    public void setPlannedDate(String plannedDate)
    {
        if (plannedDate != null && !plannedDate.isEmpty())
        {
            try
            {
                this.releaseDate = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(plannedDate);
            }
            catch (Exception ignored)
            {
                try
                {
                    this.releaseDate = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(plannedDate);
                }
                catch (Exception ignored2)
                {
                }
            }
        }
    }

    /** 状态：0=草稿,1=构建中,2=已发布,3=已归档 */
    private Integer status;

    /** 构建号 */
    private Integer buildNumber;

    /** Git标签 */
    private String gitTag;

    /** Git分支 */
    private String gitBranch;

    /** 变更日志 */
    private String changelog;

    /** 描述字段（对应desc列） */
    private String desc;

    /** 发布类型 */
    private Integer releaseType;

    /** 是否草稿 */
    private Integer isDraft;

    /** 是否稳定版 */
    private Integer isStable;

    /** 下载地址 */
    private String downloadUrl;

    /** 发布说明 */
    private String releaseNotes;

    /** 逻辑删除标记 */
    private Boolean deleted;

    /** 元数据 */
    private String metadata;

    /** 产物数量（非数据库字段） */
    private Integer artifactCount;

    /** 问题数量（非数据库字段） */
    private Integer issueCount;

    /** 状态名称（非数据库字段） */
    private String statusName;

    /** 发布类型名称（非数据库字段） */
    private String releaseTypeName;

    public Long getReleasePlanId()
    {
        return releasePlanId;
    }

    public void setReleasePlanId(Long releasePlanId)
    {
        this.releasePlanId = releasePlanId;
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

    public Date getReleaseDate()
    {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate)
    {
        this.releaseDate = releaseDate;
    }

    public Integer getStatus()
    {
        return status;
    }

    public void setStatus(Integer status)
    {
        this.status = status;
    }

    public Integer getBuildNumber()
    {
        return buildNumber;
    }

    public void setBuildNumber(Integer buildNumber)
    {
        this.buildNumber = buildNumber;
    }

    public String getGitTag()
    {
        return gitTag;
    }

    public void setGitTag(String gitTag)
    {
        this.gitTag = gitTag;
    }

    public String getGitBranch()
    {
        return gitBranch;
    }

    public void setGitBranch(String gitBranch)
    {
        this.gitBranch = gitBranch;
    }

    public String getChangelog()
    {
        return changelog;
    }

    public void setChangelog(String changelog)
    {
        this.changelog = changelog;
    }

    public String getDesc()
    {
        return desc;
    }

    public void setDesc(String desc)
    {
        this.desc = desc;
    }

    public Integer getReleaseType()
    {
        return releaseType;
    }

    public void setReleaseType(Integer releaseType)
    {
        this.releaseType = releaseType;
    }

    public Integer getIsDraft()
    {
        return isDraft;
    }

    public void setIsDraft(Integer isDraft)
    {
        this.isDraft = isDraft;
    }

    public Integer getIsStable()
    {
        return isStable;
    }

    public void setIsStable(Integer isStable)
    {
        this.isStable = isStable;
    }

    public String getDownloadUrl()
    {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl)
    {
        this.downloadUrl = downloadUrl;
    }

    public String getReleaseNotes()
    {
        return releaseNotes;
    }

    public void setReleaseNotes(String releaseNotes)
    {
        this.releaseNotes = releaseNotes;
    }

    public Boolean getDeleted()
    {
        return deleted;
    }

    public void setDeleted(Boolean deleted)
    {
        this.deleted = deleted;
    }

    public String getMetadata()
    {
        return metadata;
    }

    public void setMetadata(String metadata)
    {
        this.metadata = metadata;
    }

    public Integer getArtifactCount()
    {
        return artifactCount;
    }

    public void setArtifactCount(Integer artifactCount)
    {
        this.artifactCount = artifactCount;
    }

    public Integer getIssueCount()
    {
        return issueCount;
    }

    public void setIssueCount(Integer issueCount)
    {
        this.issueCount = issueCount;
    }

    public String getStatusName()
    {
        return statusName;
    }

    public void setStatusName(String statusName)
    {
        this.statusName = statusName;
    }

    public String getReleaseTypeName()
    {
        return releaseTypeName;
    }

    public void setReleaseTypeName(String releaseTypeName)
    {
        this.releaseTypeName = releaseTypeName;
    }
}
