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
    /**
     * 查询里程碑列表
     */
    public List<Milestone> selectMilestoneList(Long projectId);

    /**
     * 查询里程碑详情
     */
    public Milestone selectMilestoneById(Long id);

    /**
     * 新增里程碑
     */
    public int insertMilestone(Milestone milestone);

    /**
     * 修改里程碑
     */
    public int updateMilestone(Milestone milestone);

    /**
     * 删除里程碑
     */
    public int deleteMilestoneById(Long id);

    /**
     * 关联迭代到里程碑
     */
    public int addIteration(Long milestoneId, Long iterationId);

    /**
     * 查询里程碑关联的迭代ID列表
     */
    public List<Long> selectIterationIds(Long milestoneId);
}
