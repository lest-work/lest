package com.lest.modules.release.entity.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddReleaseIssueDTO {
    @NotNull(message = "Release ID cannot be null")
    private Long releaseId;

    private Long issueId;
    private Long taskId;

    @NotNull(message = "Category cannot be null")
    private Integer category;

    private String notes;
}
