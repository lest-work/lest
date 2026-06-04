package com.lest.release.service;

import java.util.List;
import com.lest.release.domain.ReleasePlan;

/**
 * 发布计划 服务层
 * 
 * @author yshan2028
 */
public interface IReleasePlanService
{
    public List<ReleasePlan> selectReleasePlanList(ReleasePlan plan);

    public ReleasePlan selectReleasePlanById(Long releasePlanId);

    public int insertReleasePlan(ReleasePlan plan);

    public int updateReleasePlan(ReleasePlan plan);

    public int deleteReleasePlanById(Long releasePlanId);

    public int publish(Long releasePlanId);

    public int archive(Long releasePlanId);

    public int restore(Long releasePlanId);

    public int startBuild(Long releasePlanId);

    public int completeBuild(Long releasePlanId, String downloadUrl);

    public List<ReleasePlan> selectUpcoming();

    public List<ReleasePlan> selectRecent(Long projectId, Integer limit);
}
