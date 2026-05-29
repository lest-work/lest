package com.lest.modules.task.entity.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 标签VO
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Data
@Builder
public class LabelVO {

    private Long id;
    private Long projectId;
    private String name;
    private String color;
    private LocalDateTime createdAt;
}
