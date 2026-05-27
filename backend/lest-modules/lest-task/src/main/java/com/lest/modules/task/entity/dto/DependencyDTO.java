package com.lest.modules.task.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 任务依赖DTO
 *
 * @author Lest
 * @since 2026-05-26
 */
@Data
public class DependencyDTO {

    @NotBlank(message = "依赖类型不能为空")
    private String type;

    @NotNull(message = "依赖任务ID不能为空")
    private Long dependencyTaskId;
}
