package com.lest.modules.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lest.modules.project.entity.domain.Iteration;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 迭代Mapper
 */
@Mapper
public interface IterationMapper extends BaseMapper<Iteration> {

    /**
     * 检查时间是否与其他迭代冲突
     */
    @Select("SELECT COUNT(*) FROM iteration " +
            "WHERE project_id = #{projectId} AND deleted = 0 " +
            "AND id != #{excludeId} " +
            "AND ((start_date <= #{endDate} AND end_date >= #{startDate}))")
    int countDateConflicts(@Param("projectId") Long projectId,
                           @Param("startDate") java.time.LocalDate startDate,
                           @Param("endDate") java.time.LocalDate endDate,
                           @Param("excludeId") Long excludeId);

    /**
     * 统计项目迭代数量
     */
    @Select("SELECT COUNT(*) FROM iteration WHERE project_id = #{projectId} AND deleted = 0")
    int countByProjectId(@Param("projectId") Long projectId);
}
