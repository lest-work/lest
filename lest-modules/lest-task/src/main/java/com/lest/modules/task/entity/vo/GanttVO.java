package com.lest.modules.task.entity.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 甘特图VO
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Data
@Builder
public class GanttVO {

    private Long id;
    private String title;
    private String taskType;
    private String priority;
    private String status;
    private Long assigneeId;
    private String assigneeName;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal estimatedHours;
    private BigDecimal actualHours;
    private Integer progress;
    private Long parentId;
    private Integer depth;
    private Boolean hasBlockers;
    private List<GanttVO> children;
}
