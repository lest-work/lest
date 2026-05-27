package com.lest.modules.release.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lest.common.base.Assert;
import com.lest.modules.release.entity.domain.ReleaseArtifact;
import com.lest.modules.release.entity.dto.CreateReleaseArtifactDTO;
import com.lest.modules.release.entity.dto.ReleaseArtifactDTO;
import com.lest.modules.release.entity.vo.ReleaseArtifactVO;
import com.lest.modules.release.mapper.ReleaseArtifactMapper;
import com.lest.modules.release.service.ReleaseArtifactService;
import com.lest.modules.release.service.ReleasePlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReleaseArtifactServiceImpl extends ServiceImpl<ReleaseArtifactMapper, ReleaseArtifact>
        implements ReleaseArtifactService {

    private final ReleasePlanService releasePlanService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(CreateReleaseArtifactDTO dto, Long userId) {
        ReleaseArtifact artifact = new ReleaseArtifact();
        artifact.setReleaseId(dto.getReleaseId());
        artifact.setArtifactName(dto.getArtifactName());
        artifact.setArtifactType(dto.getArtifactType());
        artifact.setFileName(dto.getFileName());
        artifact.setFilePath(dto.getFilePath());
        artifact.setFileSize(dto.getFileSize());
        artifact.setFileHash(dto.getFileHash());
        artifact.setDownloadUrl(dto.getDownloadUrl());
        artifact.setMetadata(dto.getMetadata());
        artifact.setDownloadCount(0);
        artifact.setUploadedBy(userId);
        baseMapper.insert(artifact);
    }

    @Override
    public Page<ReleaseArtifactVO> pageQuery(ReleaseArtifactDTO dto) {
        int pageNum = dto.getPage() != null ? dto.getPage() : 1;
        int pageSize = dto.getPageSize() != null ? dto.getPageSize() : 20;
        Page<ReleaseArtifact> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ReleaseArtifact> wrapper = new LambdaQueryWrapper<>();
        if (dto.getReleaseId() != null) {
            wrapper.eq(ReleaseArtifact::getReleaseId, dto.getReleaseId());
        }
        wrapper.orderByDesc(ReleaseArtifact::getUploadedAt);
        Page<ReleaseArtifact> resultPage = baseMapper.selectPage(page, wrapper);
        Page<ReleaseArtifactVO> voPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        return voPage;
    }

    @Override
    public void delete(Long id) {
        baseMapper.deleteById(id);
    }
}
