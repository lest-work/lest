package com.lest.modules.task.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import com.lest.common.core.web.controller.BaseController;
import com.lest.common.core.web.domain.AjaxResult;
import com.lest.modules.task.domain.Label;
import com.lest.modules.task.service.ILabelService;

/**
 * 标签管理
 * 
 * @author yshan2028
 */
@RestController
public class LabelController extends BaseController
{
    @Autowired
    private ILabelService labelService;

    /**
     * 获取项目标签列表
     */
    @GetMapping("/project/{projectId}/label/list")
    public AjaxResult list(@PathVariable Long projectId)
    {
        return success(labelService.selectLabelsByProjectId(projectId));
    }

    /**
     * 创建标签
     */
    @PostMapping("/project/{projectId}/label")
    public AjaxResult add(@PathVariable Long projectId, @RequestBody Label label)
    {
        label.setProjectId(projectId);
        return toAjax(labelService.insertLabel(label));
    }

    /**
     * 删除标签
     */
    @DeleteMapping("/label/{id}")
    public AjaxResult remove(@PathVariable Long id)
    {
        return toAjax(labelService.deleteLabelById(id));
    }
}
