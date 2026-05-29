package com.lest.modules.release.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.lest.modules.release.domain.ReleaseIssue;
import com.lest.modules.release.mapper.ReleaseIssueMapper;
import com.lest.modules.release.service.IReleaseIssueService;

/**
 * 发布关联问题 服务层实现
 * 
 * @author yshan2028
 */
@Service
public class ReleaseIssueServiceImpl implements IReleaseIssueService
{
    @Autowired
    private ReleaseIssueMapper issueMapper;

    @Override
    public List<ReleaseIssue> selectIssuesByReleaseId(Long releaseId)
    {
        return issueMapper.selectIssueList(releaseId, null, null, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertIssue(ReleaseIssue issue)
    {
        Long userId = com.lest.common.security.utils.SecurityUtils.getUserId();
        issue.setAddedBy(userId);
        issue.setAddedAt(new Date());
        return issueMapper.insert(issue);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteIssueById(Long id)
    {
        return issueMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchAddIssues(Long releaseId, Long[] taskIds, Long[] issueIds, Integer category, String notes)
    {
        Long userId = com.lest.common.security.utils.SecurityUtils.getUserId();
        int count = 0;
        if (taskIds != null)
        {
            for (Long taskId : taskIds)
            {
                ReleaseIssue issue = new ReleaseIssue();
                issue.setReleaseId(releaseId);
                issue.setTaskId(taskId);
                issue.setCategory(category);
                issue.setNotes(notes);
                issue.setAddedBy(userId);
                issue.setAddedAt(new Date());
                issueMapper.insert(issue);
                count++;
            }
        }
        if (issueIds != null)
        {
            for (Long issueId : issueIds)
            {
                ReleaseIssue issue = new ReleaseIssue();
                issue.setReleaseId(releaseId);
                issue.setIssueId(issueId);
                issue.setCategory(category);
                issue.setNotes(notes);
                issue.setAddedBy(userId);
                issue.setAddedAt(new Date());
                issueMapper.insert(issue);
                count++;
            }
        }
        return count;
    }
}
