package com.lest.modules.project.entity.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 里程碑与迭代关联实体
 */
@Data
@TableName("milestone_iteration")
public class MilestoneIteration implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 里程碑ID
     */
    private Long milestoneId;

    /**
     * 迭代ID
     */
    private Long iterationId;
}
