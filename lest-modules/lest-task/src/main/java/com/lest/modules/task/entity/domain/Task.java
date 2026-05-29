package com.lest.modules.task.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 任务实体
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Data
@TableName("task")
public class Task implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 任务ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 任务标题 */
    private String title;

    /** 任务描述 */
    private String description;

    /** 所属项目ID */
    private Long projectId;

    /** 所属迭代ID */
    private Long iterationId;

    /** 父任务ID */
    private Long parentId;

    /** 类型：story/task/bug/improvement */
    private String taskType;

    /** 优先级：p0/p1/p2/p3 */
    private String priority;

    /** 状态：todo/in_progress/completed */
    private String status;

    /** 负责人ID */
    private Long assigneeId;

    /** 开始时间 */
    private LocalDateTime startTime;

    /** 完成时间 */
    private LocalDateTime completedAt;

    /** 预估工时 */
    private BigDecimal estimatedHours;

    /** 实际工时 */
    private BigDecimal actualHours;

    /** 截止日期 */
    private LocalDate dueDate;

    /** 排序权重 */
    private Integer sort;

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
