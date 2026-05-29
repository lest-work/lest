package com.lest.modules.project.mapper;

import com.lest.modules.project.domain.MilestoneIteration;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 里程碑迭代关联Mapper
 */
@Mapper
public interface MilestoneIterationMapper
{
    MilestoneIteration selectByMilestoneIdAndIterationId(@Param("milestoneId") Long milestoneId, @Param("iterationId") Long iterationId);

    List<Long> selectIterationIdsByMilestoneId(@Param("milestoneId") Long milestoneId);

    int insert(MilestoneIteration relation);

    int deleteByMilestoneId(@Param("milestoneId") Long milestoneId);
}
