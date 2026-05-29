package com.lest.modules.system.entity.dto;

public record NoticeQueryDTO(
    Integer page,
    Integer size,
    String noticeTitle,
    Integer noticeType,
    Integer status
) {}
