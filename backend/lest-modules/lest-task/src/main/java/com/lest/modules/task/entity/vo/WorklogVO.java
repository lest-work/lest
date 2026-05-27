package com.lest.modules.task.entity.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 工时记录VO
 *
 * @author Lest
 * @since 2026-05-26
 */
@Data
@Builder
public class WorklogVO {

    private Long id;
    private Long taskId;
    private Long userId;
    private String userName;
    private String userAvatar;
    private java.math.BigDecimal hours;
    private String description;
    private LocalDate workDate;
    private LocalDateTime createdAt;
}
