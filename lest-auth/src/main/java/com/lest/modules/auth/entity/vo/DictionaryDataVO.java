package com.lest.modules.auth.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 字典数据VO（字段名严格对齐数据库 sys_dictionary_data，无 alias）
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

    /** 字典ID */
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
}
