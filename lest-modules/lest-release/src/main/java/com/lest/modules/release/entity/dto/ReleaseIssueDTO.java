package com.lest.modules.release.entity.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReleaseIssueDTO {
    private Long id;
    private Long releaseId;
    private Long issueId;
    private String issueTitle;
    private String issueKey;
    private String issueType;
    private Long taskId;
    private String taskTitle;
    private Integer category;
    private String categoryName;
    private String notes;
    private Long addedBy;
    private String addedByName;
    private LocalDateTime addedAt;
    private Integer page = 1;
    private Integer pageSize = 20;
}
