package com.lest.project.mapper;

import com.lest.project.domain.Iteration;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 迭代Mapper
 */
@Mapper
public interface IterationMapper
{
    Iteration selectById(@Param("iterationId") Long iterationId);

    List<Iteration> selectIterationList(@Param("projectId") Long projectId, @Param("status") Integer status);

    int insert(Iteration iteration);

    int updateById(Iteration iteration);

    int deleteById(@Param("iterationId") Long iterationId);

    int countDateConflicts(@Param("projectId") Long projectId,
                           @Param("startDate") Date startDate,
                           @Param("endDate") Date endDate,
                           @Param("excludeId") Long excludeId);

    int countByProjectId(@Param("projectId") Long projectId);
}
