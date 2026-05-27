package com.lest.modules.system.entity.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 系统配置DTO，用于接收前端提交的创建/更新请求
 *
 * @author yshan2028
 * @since 2026-05-26
 */
public record ConfigDTO(
    Long id,
    @NotBlank(message = "配置键不能为空")
    String configKey,
    @NotBlank(message = "配置值不能为空")
    String configValue,
    String configType,
    String configGroup,
    String description,
    Integer isSystem,
    Integer sort,
    Integer status
) {}
