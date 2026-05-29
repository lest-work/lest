package com.lest.modules.system.entity.vo;

import lombok.Builder;

@Builder
public record NoticeVO(
    Long id,
    String noticeTitle,
    Integer noticeType,
    String noticeContent,
    Integer status,
    String createBy,
    String updateBy,
    String remark
) {}
