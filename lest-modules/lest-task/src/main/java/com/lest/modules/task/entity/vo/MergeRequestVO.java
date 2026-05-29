package com.lest.modules.task.entity.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * MR VO
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Data
@Builder
public class MergeRequestVO {

    private Long id;
    private Long taskId;
    private Long mrId;
    private String mrTitle;
    private String mrStatus;
    private String repoId;
    private String source;
    private LocalDateTime createdAt;
}
