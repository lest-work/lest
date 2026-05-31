package com.lest.modules.release.service;

import java.util.List;
import com.lest.modules.release.domain.ReleaseIssue;

/**
 * 发布关联问题 服务层
 * 
 * @author yshan2028
 */
public interface IReleaseIssueService
{
    public List<ReleaseIssue> selectIssuesByReleaseId(Long releasePlanId);

    public int insertIssue(ReleaseIssue issue);

    public int deleteIssueById(Long releaseIssueId);

    public int batchAddIssues(Long releasePlanId, Long[] taskIds, Long[] issueIds, Integer category, String notes);
}
