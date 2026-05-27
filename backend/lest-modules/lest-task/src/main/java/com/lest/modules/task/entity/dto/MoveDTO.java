package com.lest.modules.task.entity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 看板移动DTO
 *
 * @author Lest
 * @since 2026-05-26
 */
@Data
public class MoveDTO {

    @NotBlank(message = "目标列不能为空")
    private String targetColumn;

    private Integer targetPosition;
}
