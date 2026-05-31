package com.lest.modules.release.service;

import java.util.List;
import com.lest.modules.release.domain.ReleaseArtifact;

/**
 * 发布产物 服务层
 * 
 * @author yshan2028
 */
public interface IReleaseArtifactService
{
    public List<ReleaseArtifact> selectArtifactsByReleaseId(Long releasePlanId);

    public int insertArtifact(ReleaseArtifact artifact);

    public int deleteArtifactById(Long releaseArtifactId);
}
