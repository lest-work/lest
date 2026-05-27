package com.lest.modules.release.entity.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReleaseArtifactVO {
    private Long id;
    private Long releaseId;
    private String releaseName;
    private String artifactName;
    private String artifactType;
    private String fileName;
    private Long fileSize;
    private String fileHash;
    private String downloadUrl;
    private Integer downloadCount;
    private Long uploadedBy;
    private String uploadedByName;
    private LocalDateTime uploadedAt;
}
