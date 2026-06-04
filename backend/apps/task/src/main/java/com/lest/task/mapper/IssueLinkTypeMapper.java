package com.lest.task.mapper;

import com.lest.task.domain.IssueLinkType;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface IssueLinkTypeMapper
{
    List<IssueLinkType> selectAll();
    List<IssueLinkType> selectAllActive();
    IssueLinkType selectById(Long typeId);
    int insert(IssueLinkType type);
    int updateById(IssueLinkType type);
    int deleteById(Long typeId);
}
