package com.lest.modules.project.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

/**
 * 里程碑创建/更新DTO
 */
@Data
public class MilestoneDTO {
    /**
     * 更新时需要
     */
    private Long id;

    /**
     * 所属项目ID
     */
    @NotNull(message = "项目ID不能为空")
    private Long projectId;

    /**
     * 里程碑名称
     */
    @NotBlank(message = "里程碑名称不能为空")
    @Pattern(regexp = "^.{1,128}$", message = "里程碑名称长度不能超过128个字符")
    private String name;

    /**
     * 里程碑描述
     */
    private String description;

    /**
     * 目标日期
     */
    @NotNull(message = "目标日期不能为空")
    private LocalDate targetDate;
}
