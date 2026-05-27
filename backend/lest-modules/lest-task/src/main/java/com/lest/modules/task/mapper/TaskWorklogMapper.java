package com.lest.modules.task.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lest.modules.task.entity.domain.TaskWorklog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 工时记录Mapper
 *
 * @author Lest
 * @since 2026-05-26
 */
@Mapper
public interface TaskWorklogMapper extends BaseMapper<TaskWorklog> {
}
