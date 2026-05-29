package com.lest.modules.task.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 工时记录实体
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Data
@TableName("task_worklog")
public class TaskWorklog implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 工时记录ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 任务ID */
    private Long taskId;

    /** 用户ID */
    private Long userId;

    /** 工时（小时） */
    private BigDecimal hours;

    /** 工作描述 */
    private String description;

    /** 工作日期 */
    private LocalDate workDate;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
