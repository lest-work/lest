package com.lest.modules.task.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 任务评论实体
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Data
@TableName("task_comment")
public class TaskComment implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 评论ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 任务ID */
    private Long taskId;

    /** 用户ID */
    private Long userId;

    /** 评论内容 */
    private String content;

    /** 父评论ID（回复） */
    private Long parentId;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /** 逻辑删除标记: 0=未删除, 1=已删除 */
    @TableLogic
    private Integer deleted;
}
