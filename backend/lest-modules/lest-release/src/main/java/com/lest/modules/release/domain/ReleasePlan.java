package com.lest.modules.release.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.lest.common.core.web.domain.BaseEntity;

/**
 * 发布计划对象 release_plan
 * 
 * @author yshan2028
 */
public class ReleasePlan extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long projectId;
    private String name;
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date releaseDate;

    /** 状态：0=草稿,1=已发布,2=构建中,3=已发布,4=已归档 */
    private Integer status;
    private Integer buildNumber;
    private String gitTag;
    private String gitBranch;
    private String changelog;
    private Long createdBy;
    private Long updatedBy;

    /** 描述 */
    private String desc;
    private Integer releaseType;
    private Integer isDraft;
    private Integer isStable;
    private String downloadUrl;
    private String releaseNotes;
    private Boolean deleted;
    private String metadata;

    /** 非数据库字段 */
    private Integer artifactCount;
    private Integer issueCount;
    private String statusName;
    private String releaseTypeName;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Date getReleaseDate() { return releaseDate; }
    public void setReleaseDate(Date releaseDate) { this.releaseDate = releaseDate; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Integer getBuildNumber() { return buildNumber; }
    public void setBuildNumber(Integer buildNumber) { this.buildNumber = buildNumber; }
    public String getGitTag() { return gitTag; }
    public void setGitTag(String gitTag) { this.gitTag = gitTag; }
    public String getGitBranch() { return gitBranch; }
    public void setGitBranch(String gitBranch) { this.gitBranch = gitBranch; }
    public String getChangelog() { return changelog; }
    public void setChangelog(String changelog) { this.changelog = changelog; }
    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    public Long getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(Long updatedBy) { this.updatedBy = updatedBy; }
    public String getDesc() { return desc; }
    public void setDesc(String desc) { this.desc = desc; }
    public Integer getReleaseType() { return releaseType; }
    public void setReleaseType(Integer releaseType) { this.releaseType = releaseType; }
    public Integer getIsDraft() { return isDraft; }
    public void setIsDraft(Integer isDraft) { this.isDraft = isDraft; }
    public Integer getIsStable() { return isStable; }
    public void setIsStable(Integer isStable) { this.isStable = isStable; }
    public String getDownloadUrl() { return downloadUrl; }
    public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }
    public String getReleaseNotes() { return releaseNotes; }
    public void setReleaseNotes(String releaseNotes) { this.releaseNotes = releaseNotes; }
    public Boolean getDeleted() { return deleted; }
    public void setDeleted(Boolean deleted) { this.deleted = deleted; }
    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
    public Integer getArtifactCount() { return artifactCount; }
    public void setArtifactCount(Integer artifactCount) { this.artifactCount = artifactCount; }
    public Integer getIssueCount() { return issueCount; }
    public void setIssueCount(Integer issueCount) { this.issueCount = issueCount; }
    public String getStatusName() { return statusName; }
    public void setStatusName(String statusName) { this.statusName = statusName; }
    public String getReleaseTypeName() { return releaseTypeName; }
    public void setReleaseTypeName(String releaseTypeName) { this.releaseTypeName = releaseTypeName; }
}
