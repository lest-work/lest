package com.lest.modules.auth.entity.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 字典数据VO，用于API返回
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DictionaryDataVO {

    /** 字典数据ID */
    private Long id;

    /** 字典类型ID */
    private Long dictId;

    /** 数据键 */
    private String dataKey;

    /** 数据值 */
    private String dataValue;

    /** 显示标签 */
    private String label;

    /** 排序号 */
    private Integer sort;

    /** 状态: 1=正常, 0=禁用 */
    private Integer status;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;

    // ===== 以下为前端期望的字段名_alias =====

    /** 前端字段 alias: 字典数据ID */
    @JsonProperty("dictDataId")
    public Long getDictDataId() { return this.id; }

    /** 前端字段 alias: 字典数据名称 */
    @JsonProperty("dictDataName")
    public String getDictDataName() { return this.label; }

    /** 前端字段 alias: 排序号 */
    @JsonProperty("sortNumber")
    public Integer getSortNumber() { return this.sort; }
}
