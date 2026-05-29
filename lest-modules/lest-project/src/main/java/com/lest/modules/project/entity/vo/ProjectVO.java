package com.lest.modules.project.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 项目VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectVO {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 项目名称
     */
    private String name;

    /**
     * 项目描述
     */
    private String description;

    /**
     * 状态：1-活跃，2-已归档
     */
    private Integer status;

    /**
     * 模板：agile / waterfall / kanban
     */
    private String template;

    /**
     * 负责人ID
     */
    private Long ownerId;

    /**
     * 负责人名称
     */
    private String ownerName;

    /**
     * 开始日期
     */
    private LocalDate startDate;

    /**
     * 结束日期
     */
    private LocalDate endDate;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
