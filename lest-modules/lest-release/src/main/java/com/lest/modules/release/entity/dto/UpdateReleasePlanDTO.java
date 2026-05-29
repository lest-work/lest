package com.lest.modules.release.entity.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateReleasePlanDTO {
    @NotNull(message = "Release ID cannot be null")
    private Long id;

    private String name;
    private String description;
    private String desc;
    private String releaseNotes;
    private String changelog;
    private String releaseDate;
    private Integer status;
    private String gitTag;
    private String gitBranch;
    private Integer releaseType;
    private Integer isDraft;
    private Integer isStable;
    private String downloadUrl;
    private Integer buildNumber;
}
