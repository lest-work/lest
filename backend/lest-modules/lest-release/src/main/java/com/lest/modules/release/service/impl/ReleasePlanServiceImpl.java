package com.lest.modules.release.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.lest.common.core.exception.ServiceException;
import com.lest.modules.release.domain.ReleasePlan;
import com.lest.modules.release.mapper.ReleaseArtifactMapper;
import com.lest.modules.release.mapper.ReleaseIssueMapper;
import com.lest.modules.release.mapper.ReleasePlanMapper;
import com.lest.modules.release.service.IReleasePlanService;

/**
 * 发布计划 服务层实现
 * 
 * @author yshan2028
 */
@Service
public class ReleasePlanServiceImpl implements IReleasePlanService
{
    public static final int STATUS_DRAFT = 0;
    public static final int STATUS_PUBLISHED = 1;
    public static final int STATUS_BUILDING = 2;
    public static final int STATUS_RELEASED = 3;
    public static final int STATUS_ARCHIVED = 4;

    @Autowired
    private ReleasePlanMapper planMapper;

    @Autowired
    private ReleaseArtifactMapper artifactMapper;

    @Autowired
    private ReleaseIssueMapper issueMapper;

    @Override
    public List<ReleasePlan> selectReleasePlanList(ReleasePlan plan)
    {
        List<ReleasePlan> list = planMapper.selectPlanList(
                plan.getProjectId(), null, plan.getStatus(), plan.getReleaseType(),
                plan.getIsDraft(), plan.getIsStable(), null, null, null, null);
        list.forEach(this::enrichPlan);
        return list;
    }

    @Override
    public ReleasePlan selectReleasePlanById(Long releasePlanId)
    {
        ReleasePlan plan = planMapper.selectById(releasePlanId);
        if (plan == null)
        {
            throw new ServiceException("发布计划不存在");
        }
        enrichPlan(plan);
        return plan;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertReleasePlan(ReleasePlan plan)
    {
        plan.setStatus(STATUS_DRAFT);
        if (plan.getBuildNumber() == null)
        {
            plan.setBuildNumber(0);
        }
        if (plan.getReleaseType() == null)
        {
            plan.setReleaseType(0);
        }
        if (plan.getIsDraft() == null)
        {
            plan.setIsDraft(1);
        }
        if (plan.getIsStable() == null)
        {
            plan.setIsStable(1);
        }
        Long userId = com.lest.common.security.utils.SecurityUtils.getUserId();
        plan.setCreateBy(String.valueOf(userId));
        return planMapper.insert(plan);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateReleasePlan(ReleasePlan plan)
    {
        ReleasePlan existing = planMapper.selectById(plan.getReleasePlanId());
        if (existing == null)
        {
            throw new ServiceException("发布计划不存在");
        }
        Long userId = com.lest.common.security.utils.SecurityUtils.getUserId();
        plan.setUpdateBy(String.valueOf(userId));
        return planMapper.updateById(plan);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteReleasePlanById(Long releasePlanId)
    {
        ReleasePlan plan = planMapper.selectById(releasePlanId);
        if (plan == null)
        {
            throw new ServiceException("发布计划不存在");
        }
        if (Integer.valueOf(STATUS_RELEASED).equals(plan.getStatus()))
        {
            throw new ServiceException("已发布的版本不能删除");
        }
        return planMapper.deleteById(releasePlanId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int publish(Long releasePlanId)
    {
        ReleasePlan plan = planMapper.selectById(releasePlanId);
        if (plan == null)
        {
            throw new ServiceException("发布计划不存在");
        }
        if (Integer.valueOf(STATUS_RELEASED).equals(plan.getStatus()))
        {
            throw new ServiceException("已经发布");
        }
        plan.setStatus(STATUS_PUBLISHED);
        plan.setIsDraft(0);
        Long userId = com.lest.common.security.utils.SecurityUtils.getUserId();
        plan.setUpdateBy(String.valueOf(userId));
        return planMapper.updateById(plan);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int archive(Long releasePlanId)
    {
        ReleasePlan plan = planMapper.selectById(releasePlanId);
        if (plan == null)
        {
            throw new ServiceException("发布计划不存在");
        }
        plan.setStatus(STATUS_ARCHIVED);
        Long userId = com.lest.common.security.utils.SecurityUtils.getUserId();
        plan.setUpdateBy(String.valueOf(userId));
        return planMapper.updateById(plan);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int restore(Long releasePlanId)
    {
        ReleasePlan plan = planMapper.selectById(releasePlanId);
        if (plan == null)
        {
            throw new ServiceException("发布计划不存在");
        }
        if (!Integer.valueOf(STATUS_ARCHIVED).equals(plan.getStatus()))
        {
            throw new ServiceException("只有已归档的版本才能恢复");
        }
        plan.setStatus(STATUS_RELEASED);
        Long userId = com.lest.common.security.utils.SecurityUtils.getUserId();
        plan.setUpdateBy(String.valueOf(userId));
        return planMapper.updateById(plan);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int startBuild(Long releasePlanId)
    {
        ReleasePlan plan = planMapper.selectById(releasePlanId);
        if (plan == null)
        {
            throw new ServiceException("发布计划不存在");
        }
        if (Integer.valueOf(STATUS_BUILDING).equals(plan.getStatus()))
        {
            throw new ServiceException("已经在构建中");
        }
        if (Integer.valueOf(STATUS_RELEASED).equals(plan.getStatus()))
        {
            throw new ServiceException("已经发布");
        }
        plan.setStatus(STATUS_BUILDING);
        plan.setBuildNumber(plan.getBuildNumber() + 1);
        Long userId = com.lest.common.security.utils.SecurityUtils.getUserId();
        plan.setUpdateBy(String.valueOf(userId));
        return planMapper.updateById(plan);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int completeBuild(Long releasePlanId, String downloadUrl)
    {
        ReleasePlan plan = planMapper.selectById(releasePlanId);
        if (plan == null)
        {
            throw new ServiceException("发布计划不存在");
        }
        if (!Integer.valueOf(STATUS_BUILDING).equals(plan.getStatus()))
        {
            throw new ServiceException("当前不在构建中");
        }
        plan.setStatus(STATUS_RELEASED);
        plan.setDownloadUrl(downloadUrl);
        Long userId = com.lest.common.security.utils.SecurityUtils.getUserId();
        plan.setUpdateBy(String.valueOf(userId));
        return planMapper.updateById(plan);
    }

    @Override
    public List<ReleasePlan> selectUpcoming()
    {
        List<ReleasePlan> list = planMapper.selectUpcoming();
        list.forEach(this::enrichPlan);
        return list;
    }

    @Override
    public List<ReleasePlan> selectRecent(Long projectId, Integer limit)
    {
        List<ReleasePlan> list = planMapper.selectRecent(projectId, limit != null ? limit : 5);
        list.forEach(this::enrichPlan);
        return list;
    }

    private void enrichPlan(ReleasePlan plan)
    {
        plan.setArtifactCount(artifactMapper.countByReleaseId(plan.getReleasePlanId()));
        plan.setIssueCount(issueMapper.countByReleaseId(plan.getReleasePlanId()));
        plan.setStatusName(getStatusName(plan.getStatus()));
        plan.setReleaseTypeName(getReleaseTypeName(plan.getReleaseType()));
    }

    private String getStatusName(Integer status)
    {
        if (status == null) return "未知";
        return switch (status) {
            case STATUS_DRAFT -> "草稿";
            case STATUS_PUBLISHED -> "待发布";
            case STATUS_BUILDING -> "构建中";
            case STATUS_RELEASED -> "已发布";
            case STATUS_ARCHIVED -> "已归档";
            default -> "未知";
        };
    }

    private String getReleaseTypeName(Integer type)
    {
        if (type == null) return "标准";
        return switch (type) {
            case 0 -> "标准";
            case 1 -> "热修复";
            case 2 -> "特性";
            case 3 -> "Beta";
            case 4 -> "Alpha";
            default -> "标准";
        };
    }
}
