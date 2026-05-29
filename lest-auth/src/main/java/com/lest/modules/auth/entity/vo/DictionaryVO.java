package com.lest.modules.auth.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 字典VO（字段名严格对齐数据库 sys_dictionary，无 alias）
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

    /** 字典数据列表（嵌套关联 sys_dictionary_data） */
    private List<DictionaryDataVO> dataList;
}
