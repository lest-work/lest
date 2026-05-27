package com.lest.modules.release.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lest.modules.release.entity.domain.ReleaseIssue;
import com.lest.modules.release.entity.dto.AddReleaseIssueDTO;
import com.lest.modules.release.entity.dto.ReleaseIssueDTO;
import com.lest.modules.release.entity.vo.ReleaseIssueVO;

public interface ReleaseIssueService extends IService<ReleaseIssue> {

    void add(AddReleaseIssueDTO dto, Long userId);

    Page<ReleaseIssueVO> pageQuery(ReleaseIssueDTO dto);

    void remove(Long id);

    void batchAdd(Long releaseId, Long[] taskIds, Long[] issueIds, Integer category, String notes, Long userId);
}
