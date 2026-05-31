package com.lest.modules.task.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import com.lest.common.core.web.controller.BaseController;
import com.lest.common.core.web.domain.AjaxResult;
import com.lest.common.log.annotation.Log;
import com.lest.common.log.enums.BusinessType;
import com.lest.common.security.annotation.RequiresPermissions;
import com.lest.modules.task.domain.Label;
import com.lest.modules.task.service.ILabelService;

/**
 * 标签管理
 * 
 * @author yshan2028
 */
@RestController
@RequestMapping("")
public class LabelController extends BaseController
{
    @Autowired
    private ILabelService labelService;

    /**
     * 获取项目标签列表
     */
    @RequiresPermissions("task:label:list")
    @GetMapping("/project/{projectId}/label/list")
    public AjaxResult list(@PathVariable Long projectId)
    {
        return success(labelService.selectLabelsByProjectId(projectId));
    }

    /**
     * 创建标签
     */
    @RequiresPermissions("task:label:add")
    @Log(title = "标签管理", businessType = BusinessType.INSERT)
    @PostMapping("/project/{projectId}/label")
    public AjaxResult add(@PathVariable Long projectId, @RequestBody Label label)
    {
        label.setProjectId(projectId);
        return toAjax(labelService.insertLabel(label));
    }

    /**
     * 删除标签
     */
    @RequiresPermissions("task:label:remove")
    @Log(title = "标签管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/label/{id}")
    public AjaxResult remove(@PathVariable Long id)
    {
        return toAjax(labelService.deleteLabelById(id));
    }
}
