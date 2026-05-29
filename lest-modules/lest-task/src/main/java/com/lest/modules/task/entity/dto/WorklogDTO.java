package com.lest.modules.task.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 工时记录DTO
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Data
public class WorklogDTO {

    @NotNull(message = "工时不能为空")
    private BigDecimal hours;

    private String description;

    @NotNull(message = "工作日期不能为空")
    private LocalDate workDate;
}
