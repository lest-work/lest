package com.lest.modules.system.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@TableName("sys_dict")
public class SysDict {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String dictName;

    private String dictCode;

    private String description;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
