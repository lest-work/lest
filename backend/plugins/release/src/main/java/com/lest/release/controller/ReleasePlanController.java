package com.lest.release.controller;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.lest.common.core.web.controller.BaseController;
import com.lest.common.core.web.domain.AjaxResult;
import com.lest.common.core.web.page.TableDataInfo;
import com.lest.common.log.annotation.Log;
import com.lest.common.log.enums.BusinessType;
import com.lest.common.security.annotation.RequireProjectRole;
import com.lest.release.domain.ReleasePlan;
import com.lest.release.domain.ReleaseArtifact;
import com.lest.release.domain.ReleaseIssue;
import com.lest.release.service.IReleasePlanService;
import com.lest.release.service.IReleaseArtifactService;
import com.lest.release.service.IReleaseIssueService;

/**
 * 发布管理
 *
 * @author yshan2028
 */
@RestController
@RequestMapping("/release")
public class ReleasePlanController extends BaseController
{
    @Autowired
    private IReleasePlanService releasePlanService;

    @Autowired
    private IReleaseArtifactService releaseArtifactService;

    @Autowired
    private IReleaseIssueService releaseIssueService;

    @GetMapping("/plan/list")
    public TableDataInfo list(ReleasePlan plan)
    {
        startPage();
        List<ReleasePlan> list = releasePlanService.selectReleasePlanList(plan);
        return getDataTable(list);
    }

    @GetMapping("/plan/{releasePlanId}")
    public AjaxResult getInfo(@PathVariable Long releasePlanId)
    {
        return success(releasePlanService.selectReleasePlanById(releasePlanId));
    }

    @PostMapping("/plan")
    public AjaxResult add(@RequestBody ReleasePlan plan)
    {
        return toAjax(releasePlanService.insertReleasePlan(plan));
    }

    @PutMapping("/plan")
    public AjaxResult edit(@RequestBody ReleasePlan plan)
    {
        return toAjax(releasePlanService.updateReleasePlan(plan));
    }

    @DeleteMapping("/plan/{releasePlanId}")
    public AjaxResult remove(@PathVariable Long releasePlanId)
    {
        return toAjax(releasePlanService.deleteReleasePlanById(releasePlanId));
    }

    @PostMapping("/plan/{releasePlanId}/publish")
    public AjaxResult publish(@PathVariable Long releasePlanId)
    {
        return toAjax(releasePlanService.publish(releasePlanId));
    }

    @PostMapping("/plan/{releasePlanId}/archive")
    public AjaxResult archive(@PathVariable Long releasePlanId)
    {
        return toAjax(releasePlanService.archive(releasePlanId));
    }

    @PostMapping("/plan/{releasePlanId}/restore")
    public AjaxResult restore(@PathVariable Long releasePlanId)
    {
        return toAjax(releasePlanService.restore(releasePlanId));
    }

    @PostMapping("/plan/{releasePlanId}/build/start")
    public AjaxResult startBuild(@PathVariable Long releasePlanId)
    {
        return toAjax(releasePlanService.startBuild(releasePlanId));
    }

    @PostMapping("/plan/{releasePlanId}/build/complete")
    public AjaxResult completeBuild(@PathVariable Long releasePlanId,
                                     @RequestParam(required = false) String downloadUrl)
    {
        return toAjax(releasePlanService.completeBuild(releasePlanId, downloadUrl));
    }

    @GetMapping("/plan/upcoming")
    public AjaxResult upcoming()
    {
        return success(releasePlanService.selectUpcoming());
    }

    @RequireProjectRole(checkMembership = true)
    @GetMapping("/{projectId}/plan/recent")
    public AjaxResult recent(@PathVariable Long projectId,
                             @RequestParam(required = false) Integer limit)
    {
        return success(releasePlanService.selectRecent(projectId, limit));
    }

    @Log(title = "发布产物", businessType = BusinessType.INSERT)
    @PostMapping("/artifact")
    public AjaxResult addArtifact(@RequestBody ReleaseArtifact artifact)
    {
        return toAjax(releaseArtifactService.insertArtifact(artifact));
    }

    @GetMapping("/artifact/list")
    public AjaxResult artifactList(@RequestParam Long releasePlanId)
    {
        return success(releaseArtifactService.selectArtifactsByReleaseId(releasePlanId));
    }

    @Log(title = "发布产物", businessType = BusinessType.DELETE)
    @DeleteMapping("/artifact/{releaseArtifactId}")
    public AjaxResult removeArtifact(@PathVariable Long releaseArtifactId)
    {
        return toAjax(releaseArtifactService.deleteArtifactById(releaseArtifactId));
    }

    @Log(title = "发布关联问题", businessType = BusinessType.INSERT)
    @PostMapping("/issue")
    public AjaxResult addIssue(@RequestBody ReleaseIssue issue)
    {
        return toAjax(releaseIssueService.insertIssue(issue));
    }

    @Log(title = "发布关联问题", businessType = BusinessType.INSERT)
    @PostMapping("/issue/batch")
    public AjaxResult batchAddIssues(@RequestParam Long releasePlanId,
                                      @RequestParam(required = false) Long[] taskIds,
                                      @RequestParam(required = false) Long[] issueIds,
                                      @RequestParam Integer category,
                                      @RequestParam(required = false) String notes)
    {
        return toAjax(releaseIssueService.batchAddIssues(releasePlanId, taskIds, issueIds, category, notes));
    }

    @GetMapping("/issue/list")
    public AjaxResult issueList(@RequestParam Long releasePlanId)
    {
        return success(releaseIssueService.selectIssuesByReleaseId(releasePlanId));
    }

    @Log(title = "发布关联问题", businessType = BusinessType.DELETE)
    @DeleteMapping("/issue/{releaseIssueId}")
    public AjaxResult removeIssue(@PathVariable Long releaseIssueId)
    {
        return toAjax(releaseIssueService.deleteIssueById(releaseIssueId));
    }
}
