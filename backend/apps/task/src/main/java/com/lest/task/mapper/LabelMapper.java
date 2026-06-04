package com.lest.task.mapper;

import com.lest.task.domain.Label;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface LabelMapper
{
    List<Label> selectByProjectId(Long projectId);

    Label selectById(Long labelId);

    int insert(Label label);

    int update(Label label);

    int deleteById(Long labelId);

    List<Label> selectLabelsByTaskId(Long taskId);
}
