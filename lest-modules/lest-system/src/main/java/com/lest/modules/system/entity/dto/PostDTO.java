package com.lest.modules.system.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PostDTO(
    Long id,
    @NotBlank(message = "岗位编码不能为空")
    @Size(min = 2, max = 64, message = "岗位编码长度必须在2-64之间")
    String postCode,
    @NotBlank(message = "岗位名称不能为空")
    @Size(min = 2, max = 50, message = "岗位名称长度必须在2-50之间")
    String postName,
    Integer sort,
    Integer status,
    String remark
) {}
