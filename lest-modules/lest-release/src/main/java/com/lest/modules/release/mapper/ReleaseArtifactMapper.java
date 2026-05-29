package com.lest.modules.release.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lest.modules.release.entity.domain.ReleaseArtifact;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReleaseArtifactMapper extends BaseMapper<ReleaseArtifact> {

    Page<ReleaseArtifact> selectArtifactPage(Page<ReleaseArtifact> page, LambdaQueryWrapper<ReleaseArtifact> wrapper);
}
