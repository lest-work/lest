package com.lest.modules.system.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 系统配置实体，对应 sys_config 表
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Getter
@Setter
@TableName("sys_config")
public class SysConfig {

    /** 配置ID，自增主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 配置键 */
    private String configKey;

    /** 配置值 */
    private String configValue;

    /** 配置类型（如：string、number、boolean、json） */
    private String configType;

    /** 配置分组 */
    private String configGroup;

    /** 配置描述 */
    private String description;

    /** 是否系统内置：0-否，1-是 */
    @TableField("is_system")
    private Integer isSystem;

    /** 排序值 */
    private Integer sort;

    /** 状态：0-禁用，1-启用 */
    private Integer status;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
