package com.lest.modules.auth.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 字典DTO，用于接收前端提交的创建/更新请求
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Data
public class DictionaryDTO {

    /** 字典ID（更新时必需） */
    @JsonProperty("dictId")
    private Long id;

    /** 字典编码 */
    @NotBlank(message = "字典编码不能为空")
    private String dictCode;

    /** 字典名称 */
    @NotBlank(message = "字典名称不能为空")
    private String dictName;

    /** 字典描述 */
    private String description;

    /** 状态: 1=正常, 0=禁用 */
    @NotNull(message = "状态不能为空")
    private Integer status;
}
