package com.lest.modules.system.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NoticeDTO(
    Long id,
    @NotBlank(message = "公告标题不能为空")
    @Size(min = 1, max = 100, message = "公告标题长度必须在1-100之间")
    String noticeTitle,
    Integer noticeType,
    String noticeContent,
    Integer status,
    String remark
) {}
