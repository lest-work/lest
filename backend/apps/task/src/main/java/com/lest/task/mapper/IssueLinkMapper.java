package com.lest.task.mapper;

import com.lest.task.domain.IssueLink;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface IssueLinkMapper
{
    IssueLink selectById(@Param("linkId") Long linkId);
    List<IssueLink> selectByTaskId(@Param("taskId") Long taskId);
    List<IssueLink> selectBySourceTaskId(@Param("sourceTaskId") Long sourceTaskId);
    List<IssueLink> selectLinksWithDetails(@Param("taskId") Long taskId);
    int insert(IssueLink link);
    int updateById(IssueLink link);
    int deleteById(@Param("linkId") Long linkId);
    int deleteByTaskId(@Param("taskId") Long taskId);
}
