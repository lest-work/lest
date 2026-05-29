package com.lest.modules.release.entity.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
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

    @TableField(exist = false)
    private String releaseName;

    @TableField(exist = false)
    private String addedByName;

    @TableLogic
    private Boolean deleted;
}
