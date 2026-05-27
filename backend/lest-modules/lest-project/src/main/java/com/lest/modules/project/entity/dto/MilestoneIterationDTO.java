package com.lest.modules.project.entity.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 里程碑关联迭代DTO
 */
@Data
public class MilestoneIterationDTO {
    /**
     * 迭代ID
     */
    @NotNull(message = "迭代ID不能为空")
    private Long iterationId;
}
