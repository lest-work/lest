package com.lest.modules.job.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 定时任务实体
 *
 * @author yshan2028
 */
@Data
@TableName("sys_job")
public class SysJob implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 任务ID */
    @TableId(type = IdType.AUTO)
    private Long jobId;

    /** 任务名称 */
    private String jobName;

    /** 任务组名 */
    private String jobGroup;

    /** 调用目标字符串 */
    private String invokeTarget;

    /** cron执行表达式 */
    private String cronExpression;

    /** 计划策略：0=默认, 1=立即执行, 2=执行一次, 3=不触发 */
    private String misfirePolicy;

    /** 是否并发执行：0=允许, 1=禁止 */
    private Integer concurrent;

    /** 状态：0=正常, 1=暂停 */
    private Integer status;

    /** 任务类型：0=Java类, 1=Spring Bean方法 */
    private Integer jobType;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /** 备注 */
    private String remark;

    /** 逻辑删除 */
    @TableLogic
    private Integer deleted;
}
