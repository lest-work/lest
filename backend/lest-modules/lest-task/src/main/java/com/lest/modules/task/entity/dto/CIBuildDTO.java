package com.lest.modules.task.entity.dto;

import lombok.Data;

/**
 * CI构建DTO（用于webhook回调）
 *
 * @author Lest
 * @since 2026-05-26
 */
@Data
public class CIBuildDTO {

    private String buildId;

    private String buildStatus;

    private String commitHash;

    private String repoId;

    private String branch;

    private String pipelineUrl;

    private Long duration;

    private String triggerType;
}
