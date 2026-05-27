package com.lest.modules.task.entity.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 任务依赖实体
 *
 * @author Lest
 * @since 2026-05-26
 */
@Data
@TableName("task_dependency")
public class TaskDependency implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 任务ID */
    private Long taskId;

    /** 依赖任务ID */
    private Long dependencyTaskId;

    /** 类型：blocker/blocked_by */
    private String type;
}
