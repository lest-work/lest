package com.lest.modules.release.entity.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReleasePlanDTO {
    private Long id;
    private Long projectId;
    private String projectName;
    private String name;
    private String description;
    private LocalDateTime releaseDate;
    private Integer status;
    private Integer buildNumber;
    private String gitTag;
    private String gitBranch;
    private String changelog;
    private Long createdBy;
    private String createdByName;
    private LocalDateTime createdAt;
    private Long updatedBy;
    private LocalDateTime updatedAt;
    private String desc;
    private Integer releaseType;
    private String releaseTypeName;
    private Integer isDraft;
    private Integer isStable;
    private String downloadUrl;
    private String releaseNotes;
    private Integer artifactCount;
    private Integer issueCount;
    private List<ReleaseArtifactDTO> artifacts;
    private List<ReleaseIssueDTO> issues;
}
