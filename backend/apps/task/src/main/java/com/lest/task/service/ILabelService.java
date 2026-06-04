package com.lest.task.service;

import java.util.List;
import com.lest.task.domain.Label;

/**
 * 标签 服务层
 *
 * @author yshan2028
 */
public interface ILabelService
{
    public List<Label> selectLabelsByProjectId(Long projectId);

    public Label selectLabelById(Long labelId);

    public int insertLabel(Label label);

    public int updateLabel(Label label);

    public int deleteLabelById(Long labelId);
}
