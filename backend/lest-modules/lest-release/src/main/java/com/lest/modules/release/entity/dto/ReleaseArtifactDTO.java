package com.lest.modules.release.entity.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReleaseArtifactDTO {
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
    private String uploadedByName;
    private LocalDateTime uploadedAt;
    private String metadata;
    private Integer page = 1;
    private Integer pageSize = 20;
}
