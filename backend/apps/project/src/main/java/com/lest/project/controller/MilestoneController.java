package com.lest.project.controller;

import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.lest.common.core.web.controller.BaseController;
import com.lest.common.core.web.domain.AjaxResult;
import com.lest.common.core.web.page.TableDataInfo;
import com.lest.common.log.annotation.Log;
import com.lest.common.log.enums.BusinessType;
import com.lest.common.security.annotation.RequireProjectRole;
import com.lest.project.domain.Milestone;
import com.lest.project.domain.MilestoneIteration;
import com.lest.project.service.IMilestoneService;

/**
 * 里程碑管理
 *
 * @author yshan2028
 */
@RestController
@RequestMapping("/milestone")
public class MilestoneController extends BaseController
{
    private final IMilestoneService milestoneService;

    public MilestoneController(IMilestoneService milestoneService)
    {
        this.milestoneService = milestoneService;
    }

    @RequireProjectRole(checkMembership = true)
    @GetMapping("/{projectId}/list")
    public TableDataInfo list(@PathVariable Long projectId)
    {
        startPage();
        List<Milestone> list = milestoneService.selectMilestoneList(projectId);
        return getDataTable(list);
    }

    @RequireProjectRole(checkMembership = true)
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id)
    {
        return success(milestoneService.selectMilestoneById(id));
    }

    @RequireProjectRole(checkMembership = true)
    @PostMapping("/{projectId}")
    public AjaxResult add(@PathVariable Long projectId, @RequestBody Milestone milestone)
    {
        milestone.setProjectId(projectId);
        return toAjax(milestoneService.insertMilestone(milestone));
    }

    @RequireProjectRole(roles = {"admin"}, checkMembership = false)
    @Log(title = "里程碑管理", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}")
    public AjaxResult edit(@PathVariable Long id, @RequestBody Milestone milestone)
    {
        milestone.setMilestoneId(id);
        return toAjax(milestoneService.updateMilestone(milestone));
    }

    @RequireProjectRole(roles = {"admin"}, checkMembership = false)
    @Log(title = "里程碑管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable Long id)
    {
        return toAjax(milestoneService.deleteMilestoneById(id));
    }

    @RequireProjectRole(roles = {"admin"}, checkMembership = false)
    @Log(title = "里程碑管理", businessType = BusinessType.UPDATE)
    @PostMapping("/{id}/iteration")
    public AjaxResult addIteration(@PathVariable Long id, @RequestBody MilestoneIteration relation)
    {
        return toAjax(milestoneService.addIteration(id, relation.getIterationId()));
    }

    @RequireProjectRole(checkMembership = true)
    @GetMapping("/{id}/iteration/list")
    public AjaxResult iterationList(@PathVariable Long id)
    {
        return success(milestoneService.selectIterationIds(id));
    }
}
