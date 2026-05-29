package com.lest.modules.system.entity.vo;

import lombok.Builder;

@Builder
public record PostVO(
    Long id,
    String postCode,
    String postName,
    Integer sort,
    Integer status,
    String remark
) {}
