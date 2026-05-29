package com.lest.modules.task.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.lest.common.core.exception.ServiceException;
import com.lest.modules.task.domain.Label;
import com.lest.modules.task.mapper.LabelMapper;
import com.lest.modules.task.service.ILabelService;

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
    public Label selectLabelById(Long id)
    {
        return labelMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertLabel(Label label)
    {
        return labelMapper.insert(label);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteLabelById(Long id)
    {
        Label label = labelMapper.selectById(id);
        if (label == null)
        {
            throw new ServiceException("标签不存在");
        }
        return labelMapper.deleteById(id);
    }
}
