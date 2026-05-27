package com.lest.modules.task.entity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 状态更新DTO
 *
 * @author Lest
 * @since 2026-05-26
 */
@Data
public class StatusDTO {

    @NotBlank(message = "状态不能为空")
    private String status;
}
