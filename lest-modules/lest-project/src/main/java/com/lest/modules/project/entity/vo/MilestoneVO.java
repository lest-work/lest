package com.lest.modules.project.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 里程碑VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MilestoneVO {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 所属项目ID
     */
    private Long projectId;

    /**
     * 里程碑名称
     */
    private String name;

    /**
     * 里程碑描述
     */
    private String description;

    /**
     * 目标日期
     */
    private LocalDate targetDate;

    /**
     * 关联的迭代列表
     */
    private List<IterationVO> iterations;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
