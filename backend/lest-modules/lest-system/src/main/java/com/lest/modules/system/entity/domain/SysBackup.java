package com.lest.modules.system.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 数据备份实体，对应 sys_backup 表
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Getter
@Setter
@TableName("sys_backup")
public class SysBackup {

    /** 备份ID，自增主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 备份名称 */
    private String backupName;

    /** 备份类型（manual-手动备份、auto-自动备份） */
    private String backupType;

    /** 备份文件路径 */
    private String backupPath;

    /** 文件大小（字节） */
    private Long fileSize;

    /** 状态（pending-进行中、success-成功、failed-失败） */
    private String status;

    /** 备份描述 */
    private String description;

    /** 创建人ID */
    private Long createdBy;

    /** 备份完成时间 */
    private LocalDateTime completedAt;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /** 逻辑删除标记 */
    @TableLogic
    private Integer deleted;
}
