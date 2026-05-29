package com.lest.modules.release.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lest.modules.release.entity.domain.ReleasePlan;
import com.lest.modules.release.entity.dto.*;
import com.lest.modules.release.entity.vo.ReleasePlanVO;

import java.util.List;

public interface ReleasePlanService extends IService<ReleasePlan> {

    void create(CreateReleasePlanDTO dto, Long userId);

    void update(UpdateReleasePlanDTO dto, Long userId);

    void delete(Long id);

    ReleasePlanDTO getById(Long id);

    Page<ReleasePlanVO> pageQuery(ReleasePlanQueryDTO dto);

    void publish(Long id, Long userId);

    void archive(Long id, Long userId);

    void restore(Long id, Long userId);

    void startBuild(Long id, Long userId);

    void completeBuild(Long id, Long userId, String downloadUrl);

    List<ReleasePlanDTO> getUpcoming();

    List<ReleasePlanDTO> getRecent(Long projectId, Integer limit);
}
