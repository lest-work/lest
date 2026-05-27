package com.lest.modules.release.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("release_issue")
public class ReleaseIssue {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long releaseId;

    private Long issueId;

    private Long taskId;

    private Integer category;

    private String notes;

    private Long addedBy;

    private LocalDateTime addedAt;

    @TableLogic
    private Boolean deleted;
}
