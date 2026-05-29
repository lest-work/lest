package com.lest.modules.release.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateReleaseArtifactDTO {
    @NotNull(message = "Release ID cannot be null")
    private Long releaseId;

    @NotBlank(message = "Artifact name cannot be blank")
    private String artifactName;

    @NotBlank(message = "Artifact type cannot be blank")
    private String artifactType;

    private String fileName;
    private String filePath;
    private Long fileSize;
    private String fileHash;
    private String downloadUrl;
    private String metadata;
}
