package com.lest.modules.task.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lest.modules.task.entity.domain.TaskWatcher;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 任务关注者Mapper
 *
 * @author Lest
 * @since 2026-05-26
 */
@Mapper
public interface TaskWatcherMapper extends BaseMapper<TaskWatcher> {

    @Select("SELECT user_id FROM task_watcher WHERE task_id = #{taskId}")
    List<Long> selectWatcherIdsByTaskId(@Param("taskId") Long taskId);

    void deleteByTaskId(@Param("taskId") Long taskId);

    void insertBatch(@Param("taskId") Long taskId, @Param("userIds") List<Long> userIds);
}
