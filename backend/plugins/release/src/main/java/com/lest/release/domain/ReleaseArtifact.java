package com.lest.release.domain;

import java.util.Date;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.lest.common.core.web.domain.BaseEntity;

/**
 * 发布产物对象 release_artifact
 *
 * @author yshan2028
 */
public class ReleaseArtifact extends BaseEntity implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 产物ID */
    private Long releaseArtifactId;

    /** 发布计划ID */
    private Long releasePlanId;

    /** 产物名称 */
    private String artifactName;

    /** 版本号 */
    private String version;

    /** 产物类型 */
    private String artifactType;

    /** 文件名 */
    private String fileName;

    /** 文件路径 */
    private String filePath;

    /** 文件大小 */
    private Long fileSize;

    /** 文件哈希 */
    private String fileHash;

    /** 文件URL */
    private String fileUrl;

    /** 下载地址 */
    private String downloadUrl;

    /** 下载次数 */
    private Integer downloadCount;

    /** 上传用户ID */
    private Long uploadedBy;

    /** 上传时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date uploadedAt;

    /** 发布计划名称（非数据库字段） */
    private String releaseName;

    /** 上传人名称（非数据库字段） */
    private String uploadedByName;

    /** 元数据 */
    private String metadata;

    /** 逻辑删除标记 */
    private Boolean deleted;

    public Long getReleaseArtifactId()
    {
        return releaseArtifactId;
    }

    public void setReleaseArtifactId(Long releaseArtifactId)
    {
        this.releaseArtifactId = releaseArtifactId;
    }

    public Long getReleasePlanId()
    {
        return releasePlanId;
    }

    public void setReleasePlanId(Long releasePlanId)
    {
        this.releasePlanId = releasePlanId;
    }

    public String getArtifactName()
    {
        return artifactName;
    }

    public void setArtifactName(String artifactName)
    {
        this.artifactName = artifactName;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public String getArtifactType()
    {
        return artifactType;
    }

    public void setArtifactType(String artifactType)
    {
        this.artifactType = artifactType;
    }

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public String getFilePath()
    {
        return filePath;
    }

    public void setFilePath(String filePath)
    {
        this.filePath = filePath;
    }

    public Long getFileSize()
    {
        return fileSize;
    }

    public void setFileSize(Long fileSize)
    {
        this.fileSize = fileSize;
    }

    public String getFileHash()
    {
        return fileHash;
    }

    public void setFileHash(String fileHash)
    {
        this.fileHash = fileHash;
    }

    public String getFileUrl()
    {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl)
    {
        this.fileUrl = fileUrl;
    }

    public String getDownloadUrl()
    {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl)
    {
        this.downloadUrl = downloadUrl;
    }

    public Integer getDownloadCount()
    {
        return downloadCount;
    }

    public void setDownloadCount(Integer downloadCount)
    {
        this.downloadCount = downloadCount;
    }

    public Long getUploadedBy()
    {
        return uploadedBy;
    }

    public void setUploadedBy(Long uploadedBy)
    {
        this.uploadedBy = uploadedBy;
    }

    public Date getUploadedAt()
    {
        return uploadedAt;
    }

    public void setUploadedAt(Date uploadedAt)
    {
        this.uploadedAt = uploadedAt;
    }

    public String getReleaseName()
    {
        return releaseName;
    }

    public void setReleaseName(String releaseName)
    {
        this.releaseName = releaseName;
    }

    public String getUploadedByName()
    {
        return uploadedByName;
    }

    public void setUploadedByName(String uploadedByName)
    {
        this.uploadedByName = uploadedByName;
    }

    public String getMetadata()
    {
        return metadata;
    }

    public void setMetadata(String metadata)
    {
        this.metadata = metadata;
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
