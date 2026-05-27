package com.lest.modules.release.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lest.modules.release.entity.domain.ReleaseArtifact;
import com.lest.modules.release.entity.dto.*;
import com.lest.modules.release.entity.vo.ReleaseArtifactVO;

public interface ReleaseArtifactService extends IService<ReleaseArtifact> {

    void create(CreateReleaseArtifactDTO dto, Long userId);

    Page<ReleaseArtifactVO> pageQuery(ReleaseArtifactDTO dto);

    void delete(Long id);
}
