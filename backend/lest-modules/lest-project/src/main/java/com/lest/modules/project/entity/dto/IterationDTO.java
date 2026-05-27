package com.lest.modules.project.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

/**
 * 迭代创建/更新DTO
 */
@Data
public class IterationDTO {
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
     * 迭代名称
     */
    @NotBlank(message = "迭代名称不能为空")
    @Pattern(regexp = "^.{1,64}$", message = "迭代名称长度不能超过64个字符")
    private String name;

    /**
     * 迭代目标
     */
    private String goal;

    /**
     * 状态：1-计划中，2-进行中，3-已完成
     */
    private Integer status;

    /**
     * 开始日期
     */
    @NotNull(message = "开始日期不能为空")
    private LocalDate startDate;

    /**
     * 结束日期
     */
    @NotNull(message = "结束日期不能为空")
    private LocalDate endDate;
}
