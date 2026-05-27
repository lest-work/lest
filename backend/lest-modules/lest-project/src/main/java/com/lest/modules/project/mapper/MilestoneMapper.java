package com.lest.modules.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lest.modules.project.entity.domain.Milestone;
import org.apache.ibatis.annotations.Mapper;

/**
 * 里程碑Mapper
 */
@Mapper
public interface MilestoneMapper extends BaseMapper<Milestone> {
}
