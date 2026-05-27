package com.lest.modules.release.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lest.common.base.Assert;
import com.lest.modules.release.common.ErrorCode;
import com.lest.modules.release.entity.domain.ReleaseArtifact;
import com.lest.modules.release.entity.domain.ReleaseIssue;
import com.lest.modules.release.entity.domain.ReleasePlan;
import com.lest.modules.release.entity.dto.*;
import com.lest.modules.release.entity.vo.ReleasePlanVO;
import com.lest.modules.release.mapper.ReleaseArtifactMapper;
import com.lest.modules.release.mapper.ReleaseIssueMapper;
import com.lest.modules.release.mapper.ReleasePlanMapper;
import com.lest.modules.release.service.ReleasePlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.lest.modules.release.common.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class ReleasePlanServiceImpl extends ServiceImpl<ReleasePlanMapper, ReleasePlan>
        implements ReleasePlanService {

    private final ReleaseArtifactMapper artifactMapper;
    private final ReleaseIssueMapper issueMapper;

    public static final int STATUS_DRAFT = 0;
    public static final int STATUS_PUBLISHED = 1;
    public static final int STATUS_BUILDING = 2;
    public static final int STATUS_RELEASED = 3;
    public static final int STATUS_ARCHIVED = 4;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(CreateReleasePlanDTO dto, Long userId) {
        ReleasePlan plan = new ReleasePlan();
        plan.setProjectId(dto.getProjectId());
        plan.setName(dto.getName());
        plan.setDescription(dto.getDescription());
        plan.setDesc(dto.getDesc());
        plan.setReleaseNotes(dto.getReleaseNotes());
        plan.setChangelog(dto.getChangelog());
        plan.setReleaseDate(LocalDate.parse(dto.getReleaseDate(), DateTimeFormatter.ISO_DATE).atStartOfDay());
        plan.setGitTag(dto.getGitTag());
        plan.setGitBranch(dto.getGitBranch());
        plan.setReleaseType(dto.getReleaseType() != null ? dto.getReleaseType() : 0);
        plan.setIsDraft(dto.getIsDraft() != null ? dto.getIsDraft() : 1);
        plan.setIsStable(dto.getIsStable() != null ? dto.getIsStable() : 1);
        plan.setStatus(STATUS_DRAFT);
        plan.setBuildNumber(0);
        plan.setCreatedBy(userId);
        plan.setCreatedAt(LocalDateTime.now());
        baseMapper.insert(plan);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UpdateReleasePlanDTO dto, Long userId) {
        ReleasePlan plan = baseMapper.selectById(dto.getId());
        Assert.notNull(plan, RELEASE_NOT_FOUND);

        if (dto.getName() != null) plan.setName(dto.getName());
        if (dto.getDescription() != null) plan.setDescription(dto.getDescription());
        if (dto.getDesc() != null) plan.setDesc(dto.getDesc());
        if (dto.getReleaseNotes() != null) plan.setReleaseNotes(dto.getReleaseNotes());
        if (dto.getChangelog() != null) plan.setChangelog(dto.getChangelog());
        if (dto.getReleaseDate() != null) plan.setReleaseDate(LocalDate.parse(dto.getReleaseDate(), DateTimeFormatter.ISO_DATE).atStartOfDay());
        if (dto.getStatus() != null) plan.setStatus(dto.getStatus());
        if (dto.getGitTag() != null) plan.setGitTag(dto.getGitTag());
        if (dto.getGitBranch() != null) plan.setGitBranch(dto.getGitBranch());
        if (dto.getReleaseType() != null) plan.setReleaseType(dto.getReleaseType());
        if (dto.getIsDraft() != null) plan.setIsDraft(dto.getIsDraft());
        if (dto.getIsStable() != null) plan.setIsStable(dto.getIsStable());
        if (dto.getDownloadUrl() != null) plan.setDownloadUrl(dto.getDownloadUrl());
        if (dto.getBuildNumber() != null) plan.setBuildNumber(dto.getBuildNumber());

        plan.setUpdatedBy(userId);
        plan.setUpdatedAt(LocalDateTime.now());
        baseMapper.updateById(plan);
    }

    @Override
    public void delete(Long id) {
        ReleasePlan plan = baseMapper.selectById(id);
        Assert.notNull(plan, RELEASE_NOT_FOUND);
        Assert.isFalse(plan.getStatus().equals(STATUS_RELEASED), RELEASE_CANNOT_DELETE_RELEASED);
        baseMapper.deleteById(id);
    }

    @Override
    public ReleasePlanDTO getById(Long id) {
        ReleasePlan plan = baseMapper.selectById(id);
        Assert.notNull(plan, RELEASE_NOT_FOUND);
        return toDTO(plan);
    }

    @Override
    public Page<ReleasePlanVO> pageQuery(ReleasePlanQueryDTO dto) {
        Page<ReleasePlan> page = new Page<>(dto.getPage(), dto.getPageSize());
        LambdaQueryWrapper<ReleasePlan> wrapper = new LambdaQueryWrapper<>();

        if (dto.getProjectId() != null) {
            wrapper.eq(ReleasePlan::getProjectId, dto.getProjectId());
        }
        if (dto.getKeyword() != null && !dto.getKeyword().isBlank()) {
            wrapper.like(ReleasePlan::getName, dto.getKeyword());
        }
        if (dto.getStatus() != null) {
            wrapper.eq(ReleasePlan::getStatus, dto.getStatus());
        }
        if (dto.getReleaseType() != null) {
            wrapper.eq(ReleasePlan::getReleaseType, dto.getReleaseType());
        }
        if (dto.getIsDraft() != null) {
            wrapper.eq(ReleasePlan::getIsDraft, dto.getIsDraft());
        }
        if (dto.getIsStable() != null) {
            wrapper.eq(ReleasePlan::getIsStable, dto.getIsStable());
        }
        if (dto.getStartDate() != null) {
            wrapper.ge(ReleasePlan::getReleaseDate, LocalDate.parse(dto.getStartDate()).atStartOfDay());
        }
        if (dto.getEndDate() != null) {
            wrapper.le(ReleasePlan::getReleaseDate, LocalDate.parse(dto.getEndDate()).atStartOfDay());
        }

        if ("release_date".equals(dto.getSortField())) {
            if ("asc".equalsIgnoreCase(dto.getSortOrder())) {
                wrapper.orderByAsc(ReleasePlan::getReleaseDate);
            } else {
                wrapper.orderByDesc(ReleasePlan::getReleaseDate);
            }
        } else if ("status".equals(dto.getSortField())) {
            if ("asc".equalsIgnoreCase(dto.getSortOrder())) {
                wrapper.orderByAsc(ReleasePlan::getStatus);
            } else {
                wrapper.orderByDesc(ReleasePlan::getStatus);
            }
        } else {
            wrapper.orderByDesc(ReleasePlan::getCreatedAt);
        }

        Page<ReleasePlan> resultPage = baseMapper.selectPage(page, wrapper);
        Page<ReleasePlanVO> voPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());

        List<ReleasePlanVO> voList = new ArrayList<>();
        for (ReleasePlan plan : resultPage.getRecords()) {
            voList.add(toVO(plan));
        }
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publish(Long id, Long userId) {
        ReleasePlan plan = baseMapper.selectById(id);
        Assert.notNull(plan, RELEASE_NOT_FOUND);
        Assert.isFalse(plan.getStatus().equals(STATUS_RELEASED), RELEASE_ALREADY_RELEASED);
        plan.setStatus(STATUS_PUBLISHED);
        plan.setIsDraft(0);
        plan.setUpdatedBy(userId);
        plan.setUpdatedAt(LocalDateTime.now());
        baseMapper.updateById(plan);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void archive(Long id, Long userId) {
        ReleasePlan plan = baseMapper.selectById(id);
        Assert.notNull(plan, RELEASE_NOT_FOUND);
        plan.setStatus(STATUS_ARCHIVED);
        plan.setUpdatedBy(userId);
        plan.setUpdatedAt(LocalDateTime.now());
        baseMapper.updateById(plan);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void restore(Long id, Long userId) {
        ReleasePlan plan = baseMapper.selectById(id);
        Assert.notNull(plan, RELEASE_NOT_FOUND);
        Assert.isFalse(!plan.getStatus().equals(STATUS_ARCHIVED), RELEASE_NOT_ARCHIVED);
        plan.setStatus(STATUS_RELEASED);
        plan.setUpdatedBy(userId);
        plan.setUpdatedAt(LocalDateTime.now());
        baseMapper.updateById(plan);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startBuild(Long id, Long userId) {
        ReleasePlan plan = baseMapper.selectById(id);
        Assert.notNull(plan, RELEASE_NOT_FOUND);
        Assert.isFalse(plan.getStatus().equals(STATUS_BUILDING), RELEASE_ALREADY_BUILDING);
        Assert.isFalse(plan.getStatus().equals(STATUS_RELEASED), RELEASE_ALREADY_RELEASED);
        plan.setStatus(STATUS_BUILDING);
        plan.setBuildNumber(plan.getBuildNumber() + 1);
        plan.setUpdatedBy(userId);
        plan.setUpdatedAt(LocalDateTime.now());
        baseMapper.updateById(plan);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeBuild(Long id, Long userId, String downloadUrl) {
        ReleasePlan plan = baseMapper.selectById(id);
        Assert.notNull(plan, RELEASE_NOT_FOUND);
        Assert.isFalse(!plan.getStatus().equals(STATUS_BUILDING), RELEASE_NOT_BUILDING);
        plan.setStatus(STATUS_RELEASED);
        plan.setDownloadUrl(downloadUrl);
        plan.setUpdatedBy(userId);
        plan.setUpdatedAt(LocalDateTime.now());
        baseMapper.updateById(plan);
    }

    @Override
    public List<ReleasePlanDTO> getUpcoming() {
        LambdaQueryWrapper<ReleasePlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(ReleasePlan::getReleaseDate, LocalDate.now())
                .in(ReleasePlan::getStatus, STATUS_PUBLISHED, STATUS_BUILDING)
                .orderByAsc(ReleasePlan::getReleaseDate)
                .last("LIMIT 10");
        return toDTOList(baseMapper.selectList(wrapper));
    }

    @Override
    public List<ReleasePlanDTO> getRecent(Long projectId, Integer limit) {
        LambdaQueryWrapper<ReleasePlan> wrapper = new LambdaQueryWrapper<>();
        if (projectId != null) {
            wrapper.eq(ReleasePlan::getProjectId, projectId);
        }
        wrapper.eq(ReleasePlan::getStatus, STATUS_RELEASED)
                .orderByDesc(ReleasePlan::getUpdatedAt)
                .last("LIMIT " + (limit != null ? limit : 5));
        return toDTOList(baseMapper.selectList(wrapper));
    }

    private ReleasePlanDTO toDTO(ReleasePlan plan) {
        ReleasePlanDTO dto = new ReleasePlanDTO();
        dto.setId(plan.getId());
        dto.setProjectId(plan.getProjectId());
        dto.setName(plan.getName());
        dto.setDescription(plan.getDescription());
        dto.setReleaseDate(plan.getReleaseDate());
        dto.setStatus(plan.getStatus());
        dto.setBuildNumber(plan.getBuildNumber());
        dto.setGitTag(plan.getGitTag());
        dto.setGitBranch(plan.getGitBranch());
        dto.setChangelog(plan.getChangelog());
        dto.setCreatedBy(plan.getCreatedBy());
        dto.setCreatedAt(plan.getCreatedAt());
        dto.setUpdatedBy(plan.getUpdatedBy());
        dto.setUpdatedAt(plan.getUpdatedAt());
        dto.setDesc(plan.getDesc());
        dto.setReleaseType(plan.getReleaseType());
        dto.setIsDraft(plan.getIsDraft());
        dto.setIsStable(plan.getIsStable());
        dto.setDownloadUrl(plan.getDownloadUrl());
        dto.setReleaseNotes(plan.getReleaseNotes());

        LambdaQueryWrapper<ReleaseArtifact> artifactWrapper = new LambdaQueryWrapper<>();
        artifactWrapper.eq(ReleaseArtifact::getReleaseId, plan.getId());
        List<ReleaseArtifact> artifacts = artifactMapper.selectList(artifactWrapper);

        LambdaQueryWrapper<ReleaseIssue> issueWrapper = new LambdaQueryWrapper<>();
        issueWrapper.eq(ReleaseIssue::getReleaseId, plan.getId());
        List<ReleaseIssue> issues = issueMapper.selectList(issueWrapper);

        dto.setArtifactCount(artifacts.size());
        dto.setIssueCount(issues.size());

        return dto;
    }

    private ReleasePlanVO toVO(ReleasePlan plan) {
        ReleasePlanVO vo = new ReleasePlanVO();
        vo.setId(plan.getId());
        vo.setProjectId(plan.getProjectId());
        vo.setName(plan.getName());
        vo.setDescription(plan.getDescription());
        vo.setReleaseDate(plan.getReleaseDate());
        vo.setStatusName(getStatusName(plan.getStatus()));
        vo.setBuildNumber(plan.getBuildNumber());
        vo.setGitTag(plan.getGitTag());
        vo.setGitBranch(plan.getGitBranch());
        vo.setChangelog(plan.getChangelog());
        vo.setCreatedBy(plan.getCreatedBy());
        vo.setCreatedAt(plan.getCreatedAt());
        vo.setUpdatedBy(plan.getUpdatedBy());
        vo.setUpdatedAt(plan.getUpdatedAt());
        vo.setReleaseTypeName(getReleaseTypeName(plan.getReleaseType()));
        vo.setIsDraft(plan.getIsDraft());
        vo.setIsStable(plan.getIsStable());
        vo.setDownloadUrl(plan.getDownloadUrl());
        vo.setReleaseNotes(plan.getReleaseNotes());

        LambdaQueryWrapper<ReleaseArtifact> artifactWrapper = new LambdaQueryWrapper<>();
        artifactWrapper.eq(ReleaseArtifact::getReleaseId, plan.getId());
        vo.setArtifactCount(artifactMapper.selectCount(artifactWrapper).intValue());

        LambdaQueryWrapper<ReleaseIssue> issueWrapper = new LambdaQueryWrapper<>();
        issueWrapper.eq(ReleaseIssue::getReleaseId, plan.getId());
        vo.setIssueCount(issueMapper.selectCount(issueWrapper).intValue());

        return vo;
    }

    private String getStatusName(Integer status) {
        if (status == null) return "Unknown";
        return switch (status) {
            case STATUS_DRAFT -> "Draft";
            case STATUS_PUBLISHED -> "Published";
            case STATUS_BUILDING -> "Building";
            case STATUS_RELEASED -> "Released";
            case STATUS_ARCHIVED -> "Archived";
            default -> "Unknown";
        };
    }

    private String getReleaseTypeName(Integer type) {
        if (type == null) return "Standard";
        return switch (type) {
            case 0 -> "Standard";
            case 1 -> "Hotfix";
            case 2 -> "Feature";
            case 3 -> "Beta";
            case 4 -> "Alpha";
            default -> "Standard";
        };
    }

    private List<ReleasePlanDTO> toDTOList(List<ReleasePlan> plans) {
        List<ReleasePlanDTO> list = new ArrayList<>();
        for (ReleasePlan plan : plans) {
            list.add(toDTO(plan));
        }
        return list;
    }
}
