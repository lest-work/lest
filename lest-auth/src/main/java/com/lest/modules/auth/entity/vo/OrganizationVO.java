package com.lest.modules.auth.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 机构VO（字段名严格对齐数据库 sys_organization，无 alias）
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationVO {

    /** 机构ID */
    private Long id;

    /** 父机构ID */
    private Long parentId;

    /** 机构名称 */
    private String orgName;

    /** 机构编码 */
    private String orgCode;

    /** 描述 */
    private String description;

    /** 排序号 */
    private Integer sort;

    /** 状态: 1=正常, 0=禁用 */
    private Integer status;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;

    /** 子机构列表（递归） */
    private List<OrganizationVO> children;
}
