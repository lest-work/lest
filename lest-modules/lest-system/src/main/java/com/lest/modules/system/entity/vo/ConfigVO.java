package com.lest.modules.system.entity.vo;

import java.time.LocalDateTime;

/**
 * 系统配置视图对象，用于API返回
 *
 * @author yshan2028
 * @since 2026-05-26
 */
public record ConfigVO(
    Long id,
    String configKey,
    String configValue,
    String configType,
    String configGroup,
    String description,
    Integer isSystem,
    Integer sort,
    Integer status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
