package com.lest.modules.task.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lest.modules.task.entity.domain.Label;
import org.apache.ibatis.annotations.Mapper;

/**
 * 标签Mapper
 *
 * @author Lest
 * @since 2026-05-26
 */
@Mapper
public interface LabelMapper extends BaseMapper<Label> {
}
