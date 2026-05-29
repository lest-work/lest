package com.lest.modules.release.mapper;

import com.lest.modules.release.domain.ReleaseArtifact;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReleaseArtifactMapper
{
    int insert(ReleaseArtifact artifact);

    int deleteById(@Param("id") Long id);

    List<ReleaseArtifact> selectByReleaseId(@Param("releaseId") Long releaseId);

    int countByReleaseId(@Param("releaseId") Long releaseId);
}
