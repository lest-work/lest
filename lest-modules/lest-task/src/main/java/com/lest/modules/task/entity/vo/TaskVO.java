package com.lest.modules.task.entity.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 任务VO
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Data
@Builder
public class TaskVO {

    private Long id;
    private String title;
    private String description;
    private Long projectId;
    private Long iterationId;
    private Long parentId;
    private String taskType;
    private String priority;
    private String status;
    private Long assigneeId;
    private String assigneeName;
    private LocalDateTime startTime;
    private LocalDateTime completedAt;
    private BigDecimal estimatedHours;
    private BigDecimal actualHours;
    private LocalDate dueDate;
    private Integer sort;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<LabelVO> labels;
    private List<Long> watcherIds;
    private Integer childCount;
    private Integer depth;
    private Boolean hasBlockers;
    private List<TaskDependencyVO> dependencies;
}
