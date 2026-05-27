package com.lest.modules.task.service;

import com.lest.common.base.PageResult;
import com.lest.modules.task.entity.dto.LabelDTO;
import com.lest.modules.task.entity.vo.LabelVO;

import java.util.List;

/**
 * 标签服务接口
 *
 * @author Lest
 * @since 2026-05-26
 */
public interface LabelService {

    /**
     * 创建标签
     */
    Long create(Long projectId, LabelDTO dto);

    /**
     * 删除标签
     */
    void delete(Long id);

    /**
     * 获取项目标签列表
     */
    List<LabelVO> getByProjectId(Long projectId);

    /**
     * 根据ID获取标签
     */
    LabelVO getById(Long id);
}
