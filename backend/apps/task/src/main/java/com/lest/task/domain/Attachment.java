package com.lest.task.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lest.common.core.web.domain.BaseEntity;
import java.io.Serializable;
import java.util.Date;

/**
 * 任务附件对象 task_attachment
 *
 * @author yshan2028
 */
public class Attachment extends BaseEntity implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 附件ID */
    private Long attachmentId;

    /** 任务ID */
    private Long taskId;

    /** 文件名 */
    private String fileName;

    /** 文件URL */
    private String fileUrl;

    /** 文件大小 */
    private Long fileSize;

    /** 文件类型 */
    private String fileType;

    /** 上传用户ID */
    private Long uploadedBy;

    /** 上传时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date uploadedAt;

    /** 是否已删除 */
    private Integer isDeleted;

    /** 版本号 */
    private Long version;

    public Long getAttachmentId()
    {
        return attachmentId;
    }

    public void setAttachmentId(Long attachmentId)
    {
        this.attachmentId = attachmentId;
    }

    public Long getTaskId()
    {
        return taskId;
    }

    public void setTaskId(Long taskId)
    {
        this.taskId = taskId;
    }

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public String getFileUrl()
    {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl)
    {
        this.fileUrl = fileUrl;
    }

    public Long getFileSize()
    {
        return fileSize;
    }

    public void setFileSize(Long fileSize)
    {
        this.fileSize = fileSize;
    }

    public String getFileType()
    {
        return fileType;
    }

    public void setFileType(String fileType)
    {
        this.fileType = fileType;
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

    public Integer getIsDeleted()
    {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted)
    {
        this.isDeleted = isDeleted;
    }

    public Long getVersion()
    {
        return version;
    }

    public void setVersion(Long version)
    {
        this.version = version;
    }
}
