package com.lest.modules.system.entity.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 数据备份DTO，用于接收前端提交的创建请求
 *
 * @author yshan2028
 * @since 2026-05-26
 */
public record BackupDTO(
    Long id,
    @NotBlank(message = "备份名称不能为空")
    String backupName,
    String backupType,
    String description
) {}
