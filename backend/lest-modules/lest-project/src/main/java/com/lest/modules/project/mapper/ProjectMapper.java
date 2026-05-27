package com.lest.modules.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lest.modules.project.entity.domain.Project;
import org.apache.ibatis.annotations.Mapper;

/**
 * 项目Mapper
 */
@Mapper
public interface ProjectMapper extends BaseMapper<Project> {
}
