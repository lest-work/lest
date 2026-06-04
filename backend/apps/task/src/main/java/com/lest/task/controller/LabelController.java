package com.lest.task.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import com.lest.common.core.web.controller.BaseController;
import com.lest.common.core.web.domain.AjaxResult;
import com.lest.common.log.annotation.Log;
import com.lest.common.log.enums.BusinessType;
import com.lest.common.security.annotation.RequiresPermissions;
import com.lest.task.domain.Label;
import com.lest.task.service.ILabelService;

/**
 * 标签管理
 *
 * @author yshan2028
 */
@RestController
@RequestMapping("/project/{projectId}/label")
public class LabelController extends BaseController
{
    @Autowired
    private ILabelService labelService;

    @RequiresPermissions("task:label:list")
    @GetMapping("/labels")
    public AjaxResult list(@PathVariable Long projectId)
    {
        return success(labelService.selectLabelsByProjectId(projectId));
    }

    @RequiresPermissions("task:label:add")
    @Log(title = "标签管理", businessType = BusinessType.INSERT)
    @PostMapping("/labels")
    public AjaxResult add(@PathVariable Long projectId, @RequestBody Label label)
    {
        label.setProjectId(projectId);
        return toAjax(labelService.insertLabel(label));
    }

    @RequiresPermissions("task:label:remove")
    @Log(title = "标签管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable Long id)
    {
        return toAjax(labelService.deleteLabelById(id));
    }

    @RequiresPermissions("task:label:edit")
    @Log(title = "标签管理", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}")
    public AjaxResult edit(@PathVariable Long id, @RequestBody Label label)
    {
        label.setLabelId(id);
        return toAjax(labelService.updateLabel(label));
    }
}
