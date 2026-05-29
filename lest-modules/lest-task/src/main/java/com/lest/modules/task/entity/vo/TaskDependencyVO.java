package com.lest.modules.task.entity.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 任务依赖VO
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Data
@Builder
public class TaskDependencyVO {

    private Long taskId;
    private Long dependencyTaskId;
    private String dependencyTaskTitle;
    private String dependencyTaskStatus;
    private String type;
}
