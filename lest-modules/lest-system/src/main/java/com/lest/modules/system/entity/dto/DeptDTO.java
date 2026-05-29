package com.lest.modules.system.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 部门DTO（映射自 OrganizationDTO，字段名与 lest-auth 保持一致）
 *
 * @author yshan2028
 * @since 2026-05-28
 */
@Data
public class DeptDTO {

    /**
     * 部门ID（更新时必需）
     */
    private Long id;

    /**
     * 父部门ID
     */
    private Long parentId;

    /**
     * 部门名称
     */
    @NotBlank(message = "部门名称不能为空")
    private String deptName;

    /**
     * 部门编码
     */
    @NotBlank(message = "部门编码不能为空")
    private String deptCode;

    /**
     * 描述
     */
    private String description;

    /**
     * 排序号
     */
    private Integer sort;

    /**
     * 负责人
     */
    private String leader;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 状态: 1=正常, 0=禁用
     */
    @NotNull(message = "状态不能为空")
    private Integer status;
}
