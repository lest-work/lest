package com.lest.modules.task.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 任务代码关联实体（用于存储Git提交和MR关联）
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Data
@TableName("task_commit")
public class TaskCommit implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 任务ID */
    private Long taskId;

    /** 提交Hash */
    private String commitHash;

    /** 提交消息 */
    private String commitMessage;

    /** 提交者 */
    private String author;

    /** 提交时间 */
    private LocalDateTime commitTime;

    /** 仓库ID */
    private String repoId;

    /** MR ID */
    private Long mrId;

    /** MR标题 */
    private String mrTitle;

    /** MR状态 */
    private String mrStatus;

    /** 关联类型：commit/mr */
    private String type;

    /** 创建方式：auto/manual */
    private String source;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
