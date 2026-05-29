package com.lest.modules.auth.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统字典类型实体，对应 sys_dictionary 表
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Data
@TableName("sys_dictionary")
public class SysDictionary implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 字典ID */
    @TableId(type = IdType.AUTO)
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
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /** 逻辑删除标记: 0=未删除, 1=已删除 */
    @TableLogic
    private Integer deleted;
}
