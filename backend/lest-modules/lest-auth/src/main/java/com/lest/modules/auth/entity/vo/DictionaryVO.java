package com.lest.modules.auth.entity.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 字典VO，用于API返回
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DictionaryVO {

    /** 字典ID */
    private Long id;

    /** 字典编码 */
    private String dictCode;

    /** 字典名称 */
    private String dictName;

    /** 字典描述 */
    private String description;

    /** 状态: 1=正常, 0=禁用 */
    private Integer status;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;

    /** 字典数据列表 */
    private java.util.List<DictionaryDataVO> dataList;

    // ===== 以下为前端期望的字段名_alias =====

    /** 前端字段 alias: 字典ID */
    @JsonProperty("dictId")
    public Long getDictId() { return this.id; }

    /** 前端字段 alias: 排序号 */
    @JsonProperty("sortNumber")
    public Integer getSortNumber() { return null; }
}
