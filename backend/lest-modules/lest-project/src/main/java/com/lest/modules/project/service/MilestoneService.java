package com.lest.modules.project.service;

import com.lest.common.base.PageResult;
import com.lest.modules.project.entity.dto.MilestoneDTO;
import com.lest.modules.project.entity.dto.MilestoneIterationDTO;
import com.lest.modules.project.entity.vo.MilestoneVO;

import java.util.List;

/**
 * 里程碑服务接口
 */
public interface MilestoneService {

    /**
     * 创建里程碑
     */
    Long create(MilestoneDTO dto);

    /**
     * 分页查询里程碑
     */
    PageResult<MilestoneVO> page(Long projectId, Integer page, Integer size);

    /**
     * 获取里程碑详情
     */
    MilestoneVO getById(Long id);

    /**
     * 更新里程碑
     */
    void update(MilestoneDTO dto);

    /**
     * 删除里程碑
     */
    void delete(Long id);

    /**
     * 关联迭代到里程碑
     */
    void addIteration(Long milestoneId, MilestoneIterationDTO dto);

    /**
     * 获取里程碑关联的迭代ID列表
     */
    List<Long> getIterationIds(Long milestoneId);
}
