package com.lest.modules.job.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 定时任务日志实体
 *
 * @author yshan2028
 */
@Data
@TableName("sys_job_log")
public class SysJobLog implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 日志ID */
    @TableId(type = IdType.AUTO)
    private Long jobLogId;

    /** 任务ID */
    private Long jobId;

    /** 任务名称 */
    private String jobName;

    /** 任务组名 */
    private String jobGroup;

    /** 调用目标字符串 */
    private String invokeTarget;

    /** 日志信息 */
    private String jobMessage;

    /** 执行状态：0=成功, 1=失败 */
    private Integer status;

    /** 异常信息 */
    private String exceptionInfo;

    /** 开始时间 */
    private LocalDateTime startTime;

    /** 停止时间 */
    private LocalDateTime stopTime;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
