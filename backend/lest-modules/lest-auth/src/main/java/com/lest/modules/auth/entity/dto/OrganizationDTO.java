package com.lest.modules.auth.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 机构DTO，用于接收前端提交的创建/更新请求
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Data
public class OrganizationDTO {

    /** 机构ID（更新时必需） */
    @JsonProperty("organizationId")
    private Long id;

    /** 父机构ID */
    private Long parentId;

    /** 机构名称 */
    @NotBlank(message = "机构名称不能为空")
    private String orgName;

    /** 机构编码 */
    @NotBlank(message = "机构编码不能为空")
    private String orgCode;

    /** 排序号 */
    private Integer sort;

    /** 状态: 1=正常, 0=禁用 */
    @NotNull(message = "状态不能为空")
    private Integer status;

    // ===== 以下为前端期望的字段名_alias =====

    /** 前端字段 alias: 机构名称 */
    @JsonProperty("organizationName")
    private String organizationName;

    /** 前端字段 alias: 父机构ID */
    @JsonProperty("parentId")
    private Long _parentId;

    public void setOrganizationName(String organizationName) {
        if (this.orgName == null && organizationName != null) {
            this.orgName = organizationName;
        }
        this.organizationName = organizationName;
    }

    public void set_parentId(Long _parentId) {
        if (this.parentId == null && _parentId != null) {
            this.parentId = _parentId;
        }
        this._parentId = _parentId;
    }
}
