package com.lest.modules.task.mapper;

import com.lest.modules.task.domain.Label;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 标签Mapper
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Mapper
public interface LabelMapper
{
    Label selectById(@Param("id") Long id);

    List<Label> selectByProjectId(@Param("projectId") Long projectId);

    int insert(Label label);

    int deleteById(@Param("id") Long id);
}
