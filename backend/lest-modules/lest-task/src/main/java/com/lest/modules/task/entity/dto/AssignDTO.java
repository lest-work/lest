package com.lest.modules.task.entity.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 任务分配DTO
 *
 * @author Lest
 * @since 2026-05-26
 */
@Data
public class AssignDTO {

    @NotNull(message = "负责人ID不能为空")
    private Long assigneeId;

    private List<Long> watcherIds;
}
