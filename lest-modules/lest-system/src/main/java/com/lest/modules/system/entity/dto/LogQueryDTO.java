package com.lest.modules.system.entity.dto;

/**
 * 操作日志查询DTO，支持多条件分页查询
 *
 * @author yshan2028
 * @since 2026-05-26
 */
public record LogQueryDTO(
    Long userId,
    String username,
    String nickname,
    String module,
    String operation,
    Integer status,
    String startTime,
    String endTime,
    Integer page,
    Integer size
) {}
