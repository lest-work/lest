package com.lest.modules.release.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateReleasePlanDTO {
    @NotNull(message = "Project ID cannot be null")
    private Long projectId;

    @NotBlank(message = "Release name cannot be blank")
    private String name;

    private String description;
    private String desc;
    private String releaseNotes;
    private String changelog;

    @NotNull(message = "Release date cannot be null")
    private String releaseDate;

    private String gitTag;
    private String gitBranch;
    private Integer releaseType;
    private Integer isDraft;
    private Integer isStable;
}
