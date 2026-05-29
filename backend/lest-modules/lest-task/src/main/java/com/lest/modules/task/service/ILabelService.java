package com.lest.modules.task.service;

import java.util.List;
import com.lest.modules.task.domain.Label;

/**
 * 标签 服务层
 * 
 * @author yshan2028
 */
public interface ILabelService
{
    /**
     * 查询项目标签列表
     */
    public List<Label> selectLabelsByProjectId(Long projectId);

    /**
     * 查询标签详情
     */
    public Label selectLabelById(Long id);

    /**
     * 新增标签
     */
    public int insertLabel(Label label);

    /**
     * 删除标签
     */
    public int deleteLabelById(Long id);
}
