package com.lest.modules.system.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 部门VO（映射自 OrganizationVO，字段名与 lest-auth 保持一致）
 *
 * @author yshan2028
 * @since 2026-05-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeptVO {

    /** 部门ID */
    private Long id;

    /** 父部门ID */
    private Long parentId;

    /** 部门名称 */
    private String deptName;

    /** 部门编码 */
    private String deptCode;

    /** 描述 */
    private String description;

    /** 排序号 */
    private Integer sort;

    /** 负责人 */
    private String leader;

    /** 联系电话 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 状态: 1=正常, 0=禁用 */
    private Integer status;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;

    /** 子部门列表（递归） */
    private List<DeptVO> children;

    /** 选中状态 */
    private Boolean checked;
}
