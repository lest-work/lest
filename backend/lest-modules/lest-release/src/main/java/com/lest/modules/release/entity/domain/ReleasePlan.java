package com.lest.modules.release.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("release_plan")
public class ReleasePlan {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long projectId;

    private String name;

    private String description;

    private LocalDateTime releaseDate;

    private Integer status;

    private Integer buildNumber;

    private String gitTag;

    private String gitBranch;

    private String changelog;

    private Long createdBy;

    private LocalDateTime createdAt;

    private Long updatedBy;

    private LocalDateTime updatedAt;

    @TableField("`desc`")
    private String desc;

    @TableField("`release_type`")
    private Integer releaseType;

    private Integer isDraft;

    private Integer isStable;

    private String downloadUrl;

    private String releaseNotes;

    @TableLogic
    private Boolean deleted;
}
