package com.lest.modules.task.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 任务创建/更新DTO
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Data
public class TaskDTO {

    private Long id;

    @NotBlank(message = "任务标题不能为空")
    private String title;

    private String description;

    @NotNull(message = "所属项目不能为空")
    private Long projectId;

    private Long iterationId;

    private Long parentId;

    @NotBlank(message = "任务类型不能为空")
    private String taskType;

    @NotBlank(message = "优先级不能为空")
    private String priority;

    private String status;

    private Long assigneeId;

    private LocalDateTime startTime;

    private LocalDateTime completedAt;

    private BigDecimal estimatedHours;

    private BigDecimal actualHours;

    private LocalDate dueDate;

    private Integer sort;

    private List<Long> labelIds;

    private List<Long> watcherIds;
}
