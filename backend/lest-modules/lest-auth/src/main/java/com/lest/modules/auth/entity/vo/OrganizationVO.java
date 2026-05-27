package com.lest.modules.auth.entity.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 机构VO，用于API返回
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

    /** 排序号 */
    private Integer sort;

    /** 状态: 1=正常, 0=禁用 */
    private Integer status;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;

    /** 子机构列表 */
    private List<OrganizationVO> children;

    // ===== 以下为前端期望的字段名_alias =====

    /** 前端字段 alias: 机构ID */
    @JsonProperty("organizationId")
    public Long getOrganizationId() { return this.id; }

    /** 前端字段 alias: 机构名称 */
    @JsonProperty("organizationName")
    public String getOrganizationName() { return this.orgName; }
}
