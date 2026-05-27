package com.lest.modules.project.service;

import com.lest.common.base.PageResult;
import com.lest.modules.project.entity.dto.IterationDTO;
import com.lest.modules.project.entity.vo.IterationVO;

import java.util.List;

/**
 * 迭代服务接口
 */
public interface IterationService {

    /**
     * 创建迭代
     */
    Long create(IterationDTO dto);

    /**
     * 分页查询迭代
     */
    PageResult<IterationVO> page(Long projectId, Integer status, Integer page, Integer size);

    /**
     * 获取迭代详情
     */
    IterationVO getById(Long id);

    /**
     * 更新迭代
     */
    void update(IterationDTO dto);

    /**
     * 删除迭代
     */
    void delete(Long id);

    /**
     * 启动迭代
     */
    void start(Long id);

    /**
     * 结束迭代
     */
    void complete(Long id);

    /**
     * 获取迭代任务列表
     */
    List<Long> getTaskIds(Long iterationId);
}
