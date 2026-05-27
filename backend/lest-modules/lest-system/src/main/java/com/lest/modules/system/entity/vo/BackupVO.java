package com.lest.modules.system.entity.vo;

import java.time.LocalDateTime;

/**
 * 数据备份视图对象，用于API返回
 *
 * @author yshan2028
 * @since 2026-05-26
 */
public record BackupVO(
    Long id,
    String backupName,
    String backupType,
    String backupPath,
    Long fileSize,
    String status,
    String description,
    Long createdBy,
    LocalDateTime completedAt,
    LocalDateTime createdAt
) {}
