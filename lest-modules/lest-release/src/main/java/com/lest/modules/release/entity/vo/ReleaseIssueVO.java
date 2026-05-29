package com.lest.modules.release.entity.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReleaseIssueVO {
    private Long id;
    private Long releaseId;
    private String releaseName;
    private Long issueId;
    private String issueTitle;
    private String issueKey;
    private Long taskId;
    private String taskTitle;
    private String categoryName;
    private String notes;
    private Long addedBy;
    private String addedByName;
    private LocalDateTime addedAt;
}
