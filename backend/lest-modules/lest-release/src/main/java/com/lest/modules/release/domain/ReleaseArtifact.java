package com.lest.modules.release.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.lest.common.core.web.domain.BaseEntity;

/**
 * 发布产物对象 release_artifact
 * 
 * @author yshan2028
 */
public class ReleaseArtifact extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long releaseArtifactId;
    private Long releasePlanId;
    private String artifactName;
    private String artifactType;
    private String fileName;
    private String filePath;
    private Long fileSize;
    private String fileHash;
    private String downloadUrl;
    private Integer downloadCount;
    private Long uploadedBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date uploadedAt;

    private String releaseName;
    private String uploadedByName;
    private String metadata;
    private Boolean deleted;

    public Long getReleaseArtifactId() { return releaseArtifactId; }
    public void setReleaseArtifactId(Long releaseArtifactId) { this.releaseArtifactId = releaseArtifactId; }
    public Long getReleasePlanId() { return releasePlanId; }
    public void setReleasePlanId(Long releasePlanId) { this.releasePlanId = releasePlanId; }
    public String getArtifactName() { return artifactName; }
    public void setArtifactName(String artifactName) { this.artifactName = artifactName; }
    public String getArtifactType() { return artifactType; }
    public void setArtifactType(String artifactType) { this.artifactType = artifactType; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    public String getFileHash() { return fileHash; }
    public void setFileHash(String fileHash) { this.fileHash = fileHash; }
    public String getDownloadUrl() { return downloadUrl; }
    public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }
    public Integer getDownloadCount() { return downloadCount; }
    public void setDownloadCount(Integer downloadCount) { this.downloadCount = downloadCount; }
    public Long getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(Long uploadedBy) { this.uploadedBy = uploadedBy; }
    public Date getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(Date uploadedAt) { this.uploadedAt = uploadedAt; }
    public String getReleaseName() { return releaseName; }
    public void setReleaseName(String releaseName) { this.releaseName = releaseName; }
    public String getUploadedByName() { return uploadedByName; }
    public void setUploadedByName(String uploadedByName) { this.uploadedByName = uploadedByName; }
    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
    public Boolean getDeleted() { return deleted; }
    public void setDeleted(Boolean deleted) { this.deleted = deleted; }
}
