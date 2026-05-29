package com.lest.modules.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lest.modules.project.entity.domain.MilestoneIteration;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 里程碑迭代关联Mapper
 */
@Mapper
public interface MilestoneIterationMapper extends BaseMapper<MilestoneIteration> {

    /**
     * 查询里程碑关联的迭代ID列表
     */
    @Select("SELECT iteration_id FROM milestone_iteration WHERE milestone_id = #{milestoneId}")
    java.util.List<Long> selectIterationIdsByMilestoneId(@Param("milestoneId") Long milestoneId);

    /**
     * 删除里程碑的所有关联
     */
    @Delete("DELETE FROM milestone_iteration WHERE milestone_id = #{milestoneId}")
    int deleteByMilestoneId(@Param("milestoneId") Long milestoneId);
}
