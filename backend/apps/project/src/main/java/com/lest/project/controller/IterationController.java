package com.lest.project.controller;

import java.util.List;
import java.util.Map;
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
import com.lest.project.domain.Iteration;
import com.lest.project.service.IIterationService;

/**
 * 迭代管理
 *
 * @author yshan2028
 */
@RestController
@RequestMapping("/iteration")
public class IterationController extends BaseController
{
    private final IIterationService iterationService;

    public IterationController(IIterationService iterationService)
    {
        this.iterationService = iterationService;
    }

    @RequireProjectRole(checkMembership = true)
    @GetMapping("/{projectId}/list")
    public TableDataInfo list(@PathVariable Long projectId, Iteration iteration)
    {
        startPage();
        List<Iteration> list = iterationService.selectIterationList(projectId, iteration.getStatus());
        return getDataTable(list);
    }

    @RequireProjectRole(checkMembership = true)
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id)
    {
        return success(iterationService.selectIterationById(id));
    }

    @RequireProjectRole(roles = {"admin"}, checkMembership = false)
    @Log(title = "迭代管理", businessType = BusinessType.INSERT)
    @PostMapping("/{projectId}")
    public AjaxResult add(@PathVariable Long projectId, @RequestBody Iteration iteration)
    {
        iteration.setProjectId(projectId);
        return toAjax(iterationService.insertIteration(iteration));
    }

    @RequireProjectRole(roles = {"admin"}, checkMembership = false)
    @Log(title = "迭代管理", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}")
    public AjaxResult edit(@PathVariable Long id, @RequestBody Iteration iteration)
    {
        iteration.setIterationId(id);
        return toAjax(iterationService.updateIteration(iteration));
    }

    @RequireProjectRole(roles = {"admin"}, checkMembership = false)
    @Log(title = "迭代管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable Long id)
    {
        return toAjax(iterationService.deleteIterationById(id));
    }

    @RequireProjectRole(roles = {"admin"}, checkMembership = false)
    @Log(title = "迭代管理", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}/start")
    public AjaxResult start(@PathVariable Long id)
    {
        return toAjax(iterationService.startIteration(id));
    }

    @RequireProjectRole(roles = {"admin"}, checkMembership = false)
    @Log(title = "迭代管理", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}/complete")
    public AjaxResult complete(@PathVariable Long id, @RequestBody(required = false) Map<String, Object> params)
    {
        return toAjax(iterationService.completeIteration(id, params));
    }

    @RequireProjectRole(checkMembership = true)
    @GetMapping("/{id}/task")
    public AjaxResult getIterationTasks(@PathVariable Long id)
    {
        return success(iterationService.getIterationTasks(id));
    }
}
