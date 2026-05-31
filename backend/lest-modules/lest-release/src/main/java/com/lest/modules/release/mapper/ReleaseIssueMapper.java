package com.lest.modules.release.mapper;

import com.lest.modules.release.domain.ReleaseIssue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReleaseIssueMapper
{
    int insert(ReleaseIssue issue);

    int deleteById(@Param("releaseIssueId") Long releaseIssueId);

    List<ReleaseIssue> selectIssueList(@Param("releasePlanId") Long releasePlanId, @Param("issueId") Long issueId,
                                       @Param("taskId") Long taskId, @Param("category") Integer category);

    int countByReleaseId(@Param("releasePlanId") Long releasePlanId);
}
