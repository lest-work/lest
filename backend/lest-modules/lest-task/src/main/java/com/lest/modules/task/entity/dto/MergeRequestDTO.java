package com.lest.modules.task.entity.dto;

import lombok.Data;

/**
 * MR DTO（用于webhook回调和手动关联）
 *
 * @author Lest
 * @since 2026-05-26
 */
@Data
public class MergeRequestDTO {

    private Long mrId;

    private String mrTitle;

    private String mrStatus;

    private String repoId;
}
