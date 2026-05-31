package com.lest.modules.project.mapper;

import com.lest.modules.project.domain.Milestone;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 里程碑Mapper
 */
@Mapper
public interface MilestoneMapper
{
    Milestone selectById(@Param("milestoneId") Long milestoneId);

    List<Milestone> selectByProjectId(@Param("projectId") Long projectId);

    int insert(Milestone milestone);

    int updateById(Milestone milestone);

    int deleteById(@Param("milestoneId") Long milestoneId);
}
