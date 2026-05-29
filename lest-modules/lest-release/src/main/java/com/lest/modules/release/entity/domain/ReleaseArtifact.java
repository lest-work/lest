package com.lest.modules.release.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("release_artifact")
public class ReleaseArtifact {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long releaseId;

    private String artifactName;

    private String artifactType;

    private String fileName;

    private String filePath;

    private Long fileSize;

    private String fileHash;

    private String downloadUrl;

    private Integer downloadCount;

    private Long uploadedBy;

    private LocalDateTime uploadedAt;

    @TableField(exist = false)
    private String releaseName;

    @TableField(exist = false)
    private String uploadedByName;

    private String metadata;

    @TableLogic
    private Boolean deleted;
}
