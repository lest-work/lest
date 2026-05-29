package com.lest.modules.project.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.lest.common.security.annotation.RequiresPermissions;
import com.lest.modules.project.domain.Milestone;
import com.lest.modules.project.domain.MilestoneIteration;
import com.lest.modules.project.service.IMilestoneService;

/**
 * 里程碑管理
 * 
 * @author yshan2028
 */
@RestController
@RequestMapping("")
public class MilestoneController extends BaseController
{
    @Autowired
    private IMilestoneService milestoneService;

    /**
     * 查询里程碑列表
     */
    @RequiresPermissions("project:milestone:list")
    @GetMapping("/{projectId}/milestone/list")
    public TableDataInfo list(@PathVariable Long projectId)
    {
        startPage();
        List<Milestone> list = milestoneService.selectMilestoneList(projectId);
        return getDataTable(list);
    }

    /**
     * 获取里程碑详情
     */
    @RequiresPermissions("project:milestone:query")
    @GetMapping("/milestone/{id}")
    public AjaxResult getInfo(@PathVariable Long id)
    {
        return success(milestoneService.selectMilestoneById(id));
    }

    /**
     * 新增里程碑
     */
    @RequiresPermissions("project:milestone:add")
    @Log(title = "里程碑管理", businessType = BusinessType.INSERT)
    @PostMapping("/{projectId}/milestone")
    public AjaxResult add(@PathVariable Long projectId, @RequestBody Milestone milestone)
    {
        milestone.setProjectId(projectId);
        return toAjax(milestoneService.insertMilestone(milestone));
    }

    /**
     * 修改里程碑
     */
    @RequiresPermissions("project:milestone:edit")
    @Log(title = "里程碑管理", businessType = BusinessType.UPDATE)
    @PutMapping("/milestone/{id}")
    public AjaxResult edit(@PathVariable Long id, @RequestBody Milestone milestone)
    {
        milestone.setId(id);
        return toAjax(milestoneService.updateMilestone(milestone));
    }

    /**
     * 删除里程碑
     */
    @RequiresPermissions("project:milestone:remove")
    @Log(title = "里程碑管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/milestone/{id}")
    public AjaxResult remove(@PathVariable Long id)
    {
        return toAjax(milestoneService.deleteMilestoneById(id));
    }

    /**
     * 关联迭代到里程碑
     */
    @RequiresPermissions("project:milestone:edit")
    @Log(title = "里程碑管理", businessType = BusinessType.UPDATE)
    @PostMapping("/milestone/{id}/iteration")
    public AjaxResult addIteration(@PathVariable Long id, @RequestBody MilestoneIteration relation)
    {
        return toAjax(milestoneService.addIteration(id, relation.getIterationId()));
    }

    /**
     * 查询里程碑关联的迭代
     */
    @GetMapping("/milestone/{id}/iteration/list")
    public AjaxResult iterationList(@PathVariable Long id)
    {
        return success(milestoneService.selectIterationIds(id));
    }
}
