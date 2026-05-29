package com.lest.modules.task.entity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 标签DTO
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Data
public class LabelDTO {

    private Long id;

    @NotBlank(message = "标签名称不能为空")
    private String name;

    @NotBlank(message = "标签颜色不能为空")
    private String color;
}
