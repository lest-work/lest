package com.lest.modules.release.mapper;

import com.lest.modules.release.domain.ReleaseIssue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReleaseIssueMapper
{
    int insert(ReleaseIssue issue);

    int deleteById(@Param("id") Long id);

    List<ReleaseIssue> selectIssueList(@Param("releaseId") Long releaseId, @Param("issueId") Long issueId,
                                       @Param("taskId") Long taskId, @Param("category") Integer category);

    int countByReleaseId(@Param("releaseId") Long releaseId);
}
