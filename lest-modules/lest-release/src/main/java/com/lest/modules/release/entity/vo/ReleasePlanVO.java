package com.lest.modules.release.entity.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReleasePlanVO {
    private Long id;
    private Long projectId;
    private String projectName;
    private String name;
    private String description;
    private LocalDateTime releaseDate;
    private String statusName;
    private Integer buildNumber;
    private String gitTag;
    private String gitBranch;
    private String changelog;
    private Long createdBy;
    private String createdByName;
    private LocalDateTime createdAt;
    private Long updatedBy;
    private LocalDateTime updatedAt;
    private String releaseTypeName;
    private Integer isDraft;
    private Integer isStable;
    private String downloadUrl;
    private String releaseNotes;
    private Integer artifactCount;
    private Integer issueCount;
}
