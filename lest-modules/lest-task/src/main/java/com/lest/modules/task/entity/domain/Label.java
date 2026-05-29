package com.lest.modules.task.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 标签实体
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Data
@TableName("label")
public class Label implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 标签ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属项目ID */
    private Long projectId;

    /** 标签名称 */
    private String name;

    /** 标签颜色 */
    private String color;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
