package com.lest.modules.release.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lest.modules.release.entity.domain.ReleaseIssue;
import com.lest.modules.release.entity.dto.AddReleaseIssueDTO;
import com.lest.modules.release.entity.dto.ReleaseIssueDTO;
import com.lest.modules.release.entity.vo.ReleaseIssueVO;
import com.lest.modules.release.mapper.ReleaseIssueMapper;
import com.lest.modules.release.service.ReleaseIssueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReleaseIssueServiceImpl extends ServiceImpl<ReleaseIssueMapper, ReleaseIssue>
        implements ReleaseIssueService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(AddReleaseIssueDTO dto, Long userId) {
        ReleaseIssue issue = new ReleaseIssue();
        issue.setReleaseId(dto.getReleaseId());
        issue.setIssueId(dto.getIssueId());
        issue.setTaskId(dto.getTaskId());
        issue.setCategory(dto.getCategory());
        issue.setNotes(dto.getNotes());
        issue.setAddedBy(userId);
        issue.setAddedAt(LocalDateTime.now());
        baseMapper.insert(issue);
    }

    @Override
    public Page<ReleaseIssueVO> pageQuery(ReleaseIssueDTO dto) {
        int pageNum = dto.getPage() != null ? dto.getPage() : 1;
        int pageSize = dto.getPageSize() != null ? dto.getPageSize() : 20;
        Page<ReleaseIssue> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ReleaseIssue> wrapper = new LambdaQueryWrapper<>();
        if (dto.getReleaseId() != null) {
            wrapper.eq(ReleaseIssue::getReleaseId, dto.getReleaseId());
        }
        wrapper.orderByDesc(ReleaseIssue::getAddedAt);
        Page<ReleaseIssue> resultPage = baseMapper.selectPage(page, wrapper);
        Page<ReleaseIssueVO> voPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        return voPage;
    }

    @Override
    public void remove(Long id) {
        baseMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchAdd(Long releaseId, Long[] taskIds, Long[] issueIds, Integer category, String notes, Long userId) {
        List<ReleaseIssue> issues = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        if (taskIds != null) {
            for (Long taskId : taskIds) {
                ReleaseIssue issue = new ReleaseIssue();
                issue.setReleaseId(releaseId);
                issue.setTaskId(taskId);
                issue.setCategory(category);
                issue.setNotes(notes);
                issue.setAddedBy(userId);
                issue.setAddedAt(now);
                issues.add(issue);
            }
        }

        if (issueIds != null) {
            for (Long issueId : issueIds) {
                ReleaseIssue issue = new ReleaseIssue();
                issue.setReleaseId(releaseId);
                issue.setIssueId(issueId);
                issue.setCategory(category);
                issue.setNotes(notes);
                issue.setAddedBy(userId);
                issue.setAddedAt(now);
                issues.add(issue);
            }
        }

        if (!issues.isEmpty()) {
            saveBatch(issues);
        }
    }
}
