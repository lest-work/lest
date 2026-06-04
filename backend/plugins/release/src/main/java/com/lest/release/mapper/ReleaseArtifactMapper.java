package com.lest.release.mapper;

import com.lest.release.domain.ReleaseArtifact;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReleaseArtifactMapper
{
    int insert(ReleaseArtifact artifact);

    int deleteById(@Param("releaseArtifactId") Long releaseArtifactId);

    List<ReleaseArtifact> selectByReleaseId(@Param("releasePlanId") Long releasePlanId);

    int countByReleaseId(@Param("releasePlanId") Long releasePlanId);
}
