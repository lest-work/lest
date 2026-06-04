package com.lest.release.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.lest.release.domain.ReleaseArtifact;
import com.lest.release.mapper.ReleaseArtifactMapper;
import com.lest.release.service.IReleaseArtifactService;

/**
 * 发布产物 服务层实现
 * 
 * @author yshan2028
 */
@Service
public class ReleaseArtifactServiceImpl implements IReleaseArtifactService
{
    @Autowired
    private ReleaseArtifactMapper artifactMapper;

    @Override
    public List<ReleaseArtifact> selectArtifactsByReleaseId(Long releasePlanId)
    {
        return artifactMapper.selectByReleaseId(releasePlanId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertArtifact(ReleaseArtifact artifact)
    {
        Long userId = com.lest.common.security.utils.SecurityUtils.getUserId();
        artifact.setUploadedBy(userId);
        return artifactMapper.insert(artifact);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteArtifactById(Long releaseArtifactId)
    {
        return artifactMapper.deleteById(releaseArtifactId);
    }
}
