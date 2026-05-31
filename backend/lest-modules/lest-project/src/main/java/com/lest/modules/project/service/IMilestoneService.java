package com.lest.modules.project.service;

import java.util.List;
import com.lest.modules.project.domain.Milestone;

/**
 * 里程碑 服务层
 *
 * @author yshan2028
 */
public interface IMilestoneService
{
    public List<Milestone> selectMilestoneList(Long projectId);

    public Milestone selectMilestoneById(Long milestoneId);

    public int insertMilestone(Milestone milestone);

    public int updateMilestone(Milestone milestone);

    public int deleteMilestoneById(Long milestoneId);

    public int addIteration(Long milestoneId, Long iterationId);

    public List<Long> selectIterationIds(Long milestoneId);
}
