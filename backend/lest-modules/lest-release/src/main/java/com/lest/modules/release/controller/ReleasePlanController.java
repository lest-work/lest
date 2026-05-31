package com.lest.modules.release.controller;

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
import com.lest.modules.release.domain.ReleasePlan;
import com.lest.modules.release.domain.ReleaseArtifact;
import com.lest.modules.release.domain.ReleaseIssue;
import com.lest.modules.release.service.IReleasePlanService;
import com.lest.modules.release.service.IReleaseArtifactService;
import com.lest.modules.release.service.IReleaseIssueService;

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

    /**
     * 查询发布计划列表
     */
    @GetMapping("/plan/list")
    public TableDataInfo list(ReleasePlan plan)
    {
        startPage();
        List<ReleasePlan> list = releasePlanService.selectReleasePlanList(plan);
        return getDataTable(list);
    }

    /**
     * 获取发布计划详情
     */
    @GetMapping("/plan/{releasePlanId}")
    public AjaxResult getInfo(@PathVariable Long releasePlanId)
    {
        return success(releasePlanService.selectReleasePlanById(releasePlanId));
    }

    /**
     * 新增发布计划
     */
    @PostMapping("/plan")
    public AjaxResult add(@RequestBody ReleasePlan plan)
    {
        return toAjax(releasePlanService.insertReleasePlan(plan));
    }

    /**
     * 修改发布计划
     */
    @PutMapping("/plan")
    public AjaxResult edit(@RequestBody ReleasePlan plan)
    {
        return toAjax(releasePlanService.updateReleasePlan(plan));
    }

    /**
     * 删除发布计划
     */
    @DeleteMapping("/plan/{releasePlanId}")
    public AjaxResult remove(@PathVariable Long releasePlanId)
    {
        return toAjax(releasePlanService.deleteReleasePlanById(releasePlanId));
    }

    /**
     * 发布
     */
    @PostMapping("/plan/{releasePlanId}/publish")
    public AjaxResult publish(@PathVariable Long releasePlanId)
    {
        return toAjax(releasePlanService.publish(releasePlanId));
    }

    /**
     * 归档
     */
    @PostMapping("/plan/{releasePlanId}/archive")
    public AjaxResult archive(@PathVariable Long releasePlanId)
    {
        return toAjax(releasePlanService.archive(releasePlanId));
    }

    /**
     * 恢复
     */
    @PostMapping("/plan/{releasePlanId}/restore")
    public AjaxResult restore(@PathVariable Long releasePlanId)
    {
        return toAjax(releasePlanService.restore(releasePlanId));
    }

    /**
     * 开始构建
     */
    @PostMapping("/plan/{releasePlanId}/build/start")
    public AjaxResult startBuild(@PathVariable Long releasePlanId)
    {
        return toAjax(releasePlanService.startBuild(releasePlanId));
    }

    /**
     * 完成构建
     */
    @PostMapping("/plan/{releasePlanId}/build/complete")
    public AjaxResult completeBuild(@PathVariable Long releasePlanId,
                                     @RequestParam(required = false) String downloadUrl)
    {
        return toAjax(releasePlanService.completeBuild(releasePlanId, downloadUrl));
    }

    /**
     * 即将到来的发布
     */
    @GetMapping("/plan/upcoming")
    public AjaxResult upcoming()
    {
        return success(releasePlanService.selectUpcoming());
    }

    /**
     * 最近的发布
     */
    @GetMapping("/plan/recent")
    public AjaxResult recent(@RequestParam(required = false) Long projectId,
                             @RequestParam(required = false) Integer limit)
    {
        return success(releasePlanService.selectRecent(projectId, limit));
    }

    /**
     * 新增产物
     */
    @PostMapping("/artifact")
    public AjaxResult addArtifact(@RequestBody ReleaseArtifact artifact)
    {
        return toAjax(releaseArtifactService.insertArtifact(artifact));
    }

    /**
     * 获取产物列表
     */
    @GetMapping("/artifact/list")
    public AjaxResult artifactList(@RequestParam Long releasePlanId)
    {
        return success(releaseArtifactService.selectArtifactsByReleaseId(releasePlanId));
    }

    /**
     * 删除产物
     */
    @DeleteMapping("/artifact/{releaseArtifactId}")
    public AjaxResult removeArtifact(@PathVariable Long releaseArtifactId)
    {
        return toAjax(releaseArtifactService.deleteArtifactById(releaseArtifactId));
    }

    /**
     * 新增关联问题
     */
    @PostMapping("/issue")
    public AjaxResult addIssue(@RequestBody ReleaseIssue issue)
    {
        return toAjax(releaseIssueService.insertIssue(issue));
    }

    /**
     * 批量新增关联问题
     */
    @PostMapping("/issue/batch")
    public AjaxResult batchAddIssues(@RequestParam Long releasePlanId,
                                      @RequestParam(required = false) Long[] taskIds,
                                      @RequestParam(required = false) Long[] issueIds,
                                      @RequestParam Integer category,
                                      @RequestParam(required = false) String notes)
    {
        return toAjax(releaseIssueService.batchAddIssues(releasePlanId, taskIds, issueIds, category, notes));
    }

    /**
     * 获取关联问题列表
     */
    @GetMapping("/issue/list")
    public AjaxResult issueList(@RequestParam Long releasePlanId)
    {
        return success(releaseIssueService.selectIssuesByReleaseId(releasePlanId));
    }

    /**
     * 删除关联问题
     */
    @DeleteMapping("/issue/{releaseIssueId}")
    public AjaxResult removeIssue(@PathVariable Long releaseIssueId)
    {
        return toAjax(releaseIssueService.deleteIssueById(releaseIssueId));
    }
}
