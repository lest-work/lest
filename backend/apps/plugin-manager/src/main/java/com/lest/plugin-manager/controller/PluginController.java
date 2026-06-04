package com.lest.plugin.manager.controller;

import com.lest.common.core.web.controller.BaseController;
import com.lest.common.core.web.domain.AjaxResult;
import com.lest.common.core.web.page.TableDataInfo;
import com.lest.plugin.manager.domain.Plugin;
import com.lest.plugin.manager.domain.PluginConfig;
import com.lest.plugin.manager.service.IPluginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/plugin")
public class PluginController extends BaseController
{
    @Autowired
    private IPluginService pluginService;

    @GetMapping("/list")
    public TableDataInfo list(Plugin plugin)
    {
        startPage();
        List<Plugin> list = pluginService.selectPluginList(plugin);
        return getDataTable(list);
    }

    @GetMapping("/enabled")
    public AjaxResult enabled()
    {
        return success(pluginService.selectEnabledPlugins());
    }

    @GetMapping("/{pluginId}")
    public AjaxResult get(@PathVariable Long pluginId)
    {
        return success(pluginService.selectPluginById(pluginId));
    }

    @PostMapping
    public AjaxResult install(@RequestBody Plugin plugin)
    {
        Plugin installed = pluginService.installPlugin(plugin);
        return success(installed);
    }

    @DeleteMapping("/{pluginId}")
    public AjaxResult uninstall(@PathVariable Long pluginId)
    {
        return toAjax(pluginService.uninstallPlugin(pluginId));
    }

    @PutMapping("/{pluginId}/enable")
    public AjaxResult enable(@PathVariable Long pluginId)
    {
        return toAjax(pluginService.enablePlugin(pluginId));
    }

    @PutMapping("/{pluginId}/disable")
    public AjaxResult disable(@PathVariable Long pluginId)
    {
        return toAjax(pluginService.disablePlugin(pluginId));
    }

    @GetMapping("/{pluginId}/config")
    public AjaxResult getConfig(@PathVariable Long pluginId)
    {
        return success(pluginService.getPluginConfig(pluginId));
    }

    @PostMapping("/config")
    public AjaxResult setConfig(@RequestBody PluginConfig config)
    {
        return toAjax(pluginService.setPluginConfig(config));
    }
}
