package com.lest.modules.release.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lest.modules.release.entity.domain.ReleaseArtifact;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReleaseArtifactMapper extends BaseMapper<ReleaseArtifact> {
}
