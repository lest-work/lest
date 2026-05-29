package com.lest.modules.project.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

/**
 * 项目创建/更新DTO
 */
@Data
public class ProjectDTO {
    /**
     * 更新时需要
     */
    private Long id;

    /**
     * 项目名称
     */
    @NotBlank(message = "项目名称不能为空")
    @Pattern(regexp = "^.{1,128}$", message = "项目名称长度不能超过128个字符")
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
    @Pattern(regexp = "^(agile|waterfall|kanban)?$", message = "模板只能是 agile、waterfall 或 kanban")
    private String template;

    /**
     * 负责人ID
     */
    @NotNull(message = "负责人ID不能为空")
    private Long ownerId;

    /**
     * 开始日期
     */
    private LocalDate startDate;

    /**
     * 结束日期
     */
    private LocalDate endDate;
}
