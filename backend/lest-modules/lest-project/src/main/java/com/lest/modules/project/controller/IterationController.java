package com.lest.modules.project.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.lest.common.core.web.controller.BaseController;
import com.lest.common.core.web.domain.AjaxResult;
import com.lest.common.core.web.page.TableDataInfo;
import com.lest.modules.project.domain.Iteration;
import com.lest.modules.project.service.IIterationService;

/**
 * 迭代管理
 * 
 * @author yshan2028
 */
@RestController
public class IterationController extends BaseController
{
    @Autowired
    private IIterationService iterationService;

    /**
     * 查询迭代列表
     */
    @GetMapping("/{projectId}/iteration/list")
    public TableDataInfo list(@PathVariable Long projectId, Iteration iteration)
    {
        startPage();
        List<Iteration> list = iterationService.selectIterationList(projectId, iteration.getStatus());
        return getDataTable(list);
    }

    /**
     * 获取迭代详情
     */
    @GetMapping("/iteration/{id}")
    public AjaxResult getInfo(@PathVariable Long id)
    {
        return success(iterationService.selectIterationById(id));
    }

    /**
     * 新增迭代
     */
    @PostMapping("/{projectId}/iteration")
    public AjaxResult add(@PathVariable Long projectId, @RequestBody Iteration iteration)
    {
        iteration.setProjectId(projectId);
        return toAjax(iterationService.insertIteration(iteration));
    }

    /**
     * 修改迭代
     */
    @PutMapping("/iteration/{id}")
    public AjaxResult edit(@PathVariable Long id, @RequestBody Iteration iteration)
    {
        iteration.setId(id);
        return toAjax(iterationService.updateIteration(iteration));
    }

    /**
     * 删除迭代
     */
    @DeleteMapping("/iteration/{id}")
    public AjaxResult remove(@PathVariable Long id)
    {
        return toAjax(iterationService.deleteIterationById(id));
    }

    /**
     * 启动迭代
     */
    @PutMapping("/iteration/{id}/start")
    public AjaxResult start(@PathVariable Long id)
    {
        return toAjax(iterationService.startIteration(id));
    }

    /**
     * 完成迭代
     */
    @PutMapping("/iteration/{id}/complete")
    public AjaxResult complete(@PathVariable Long id)
    {
        return toAjax(iterationService.completeIteration(id));
    }
}
