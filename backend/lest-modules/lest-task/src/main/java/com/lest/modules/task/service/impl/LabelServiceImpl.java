package com.lest.modules.task.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lest.common.base.Assert;
import com.lest.modules.task.common.ErrorCode;
import com.lest.modules.task.entity.domain.Label;
import com.lest.modules.task.entity.dto.LabelDTO;
import com.lest.modules.task.entity.vo.LabelVO;
import com.lest.modules.task.mapper.LabelMapper;
import com.lest.modules.task.service.LabelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 标签服务实现
 *
 * @author Lest
 * @since 2026-05-26
 */
@Slf4j
@Service
public class LabelServiceImpl implements LabelService {

    private final LabelMapper labelMapper;

    public LabelServiceImpl(LabelMapper labelMapper) {
        this.labelMapper = labelMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(Long projectId, LabelDTO dto) {
        Label label = new Label();
        label.setProjectId(projectId);
        label.setName(dto.getName());
        label.setColor(dto.getColor());

        labelMapper.insert(label);

        log.info("创建标签: labelId={}, name={}", label.getId(), dto.getName());
        return label.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Label label = labelMapper.selectById(id);
        Assert.notNull(label, "LABEL_NOT_FOUND");

        labelMapper.deleteById(id);
        log.info("删除标签: labelId={}", id);
    }

    @Override
    public List<LabelVO> getByProjectId(Long projectId) {
        LambdaQueryWrapper<Label> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Label::getProjectId, projectId);
        wrapper.orderByAsc(Label::getCreatedAt);

        return labelMapper.selectList(wrapper).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public LabelVO getById(Long id) {
        Label label = labelMapper.selectById(id);
        Assert.notNull(label, "LABEL_NOT_FOUND");
        return convertToVO(label);
    }

    private LabelVO convertToVO(Label label) {
        return LabelVO.builder()
                .id(label.getId())
                .projectId(label.getProjectId())
                .name(label.getName())
                .color(label.getColor())
                .createdAt(label.getCreatedAt())
                .build();
    }
}
