package com.lest.modules.release.mapper;

import com.lest.modules.release.domain.ReleasePlan;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ReleasePlanMapper
{
    ReleasePlan selectById(@Param("id") Long id);

    List<ReleasePlan> selectPlanList(@Param("projectId") Long projectId, @Param("keyword") String keyword,
                                     @Param("status") Integer status, @Param("releaseType") Integer releaseType,
                                     @Param("isDraft") Integer isDraft, @Param("isStable") Integer isStable,
                                     @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate,
                                     @Param("sortField") String sortField, @Param("sortOrder") String sortOrder);

    List<ReleasePlan> selectUpcoming();

    List<ReleasePlan> selectRecent(@Param("projectId") Long projectId, @Param("limit") Integer limit);

    int insert(ReleasePlan plan);

    int updateById(ReleasePlan plan);

    int deleteById(@Param("id") Long id);
}
