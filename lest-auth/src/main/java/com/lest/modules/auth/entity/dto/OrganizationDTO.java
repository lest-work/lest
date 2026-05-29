package com.lest.modules.auth.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 机构DTO（字段名严格对齐数据库 sys_organization，无 alias）
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Data
public class OrganizationDTO {

    /** 机构ID（更新时必需） */
    private Long id;

    /** 父机构ID */
    private Long parentId;

    /** 机构名称 */
    @NotBlank(message = "机构名称不能为空")
    private String orgName;

    /** 机构编码 */
    @NotBlank(message = "机构编码不能为空")
    private String orgCode;

    /** 描述 */
    private String description;

    /** 排序号 */
    private Integer sort;

    /** 状态: 1=正常, 0=禁用 */
    @NotNull(message = "状态不能为空")
    private Integer status;
}
