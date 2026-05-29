package com.lest.modules.task.entity.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 任务关注者实体
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Data
@TableName("task_watcher")
public class TaskWatcher implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 任务ID */
    private Long taskId;

    /** 用户ID */
    private Long userId;
}
