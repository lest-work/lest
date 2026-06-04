package com.lest.task.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.lest.common.core.exception.ServiceException;
import com.lest.task.domain.Label;
import com.lest.task.mapper.LabelMapper;
import com.lest.task.service.ILabelService;

/**
 * 标签 服务层实现
 *
 * @author yshan2028
 */
@Service
public class LabelServiceImpl implements ILabelService
{
    @Autowired
    private LabelMapper labelMapper;

    @Override
    public List<Label> selectLabelsByProjectId(Long projectId)
    {
        return labelMapper.selectByProjectId(projectId);
    }

    @Override
    public Label selectLabelById(Long labelId)
    {
        return labelMapper.selectById(labelId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertLabel(Label label)
    {
        return labelMapper.insert(label);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateLabel(Label label)
    {
        Label existing = labelMapper.selectById(label.getLabelId());
        if (existing == null)
        {
            throw new ServiceException("标签不存在");
        }
        return labelMapper.update(label);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteLabelById(Long labelId)
    {
        Label label = labelMapper.selectById(labelId);
        if (label == null)
        {
            throw new ServiceException("标签不存在");
        }
        return labelMapper.deleteById(labelId);
    }
}
