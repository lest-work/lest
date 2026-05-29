package com.lest.modules.release.service;

import java.util.List;
import com.lest.modules.release.domain.ReleasePlan;

/**
 * 发布计划 服务层
 * 
 * @author yshan2028
 */
public interface IReleasePlanService
{
    public List<ReleasePlan> selectReleasePlanList(ReleasePlan plan);

    public ReleasePlan selectReleasePlanById(Long id);

    public int insertReleasePlan(ReleasePlan plan);

    public int updateReleasePlan(ReleasePlan plan);

    public int deleteReleasePlanById(Long id);

    public int publish(Long id);

    public int archive(Long id);

    public int restore(Long id);

    public int startBuild(Long id);

    public int completeBuild(Long id, String downloadUrl);

    public List<ReleasePlan> selectUpcoming();

    public List<ReleasePlan> selectRecent(Long projectId, Integer limit);
}
