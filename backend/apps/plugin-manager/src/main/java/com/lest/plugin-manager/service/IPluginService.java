package com.lest.plugin.manager.service;

import com.lest.plugin.manager.domain.Plugin;
import com.lest.plugin.manager.domain.PluginConfig;
import java.util.List;

public interface IPluginService
{
    List<Plugin> selectPluginList(Plugin plugin);
    Plugin selectPluginById(Long pluginId);
    List<Plugin> selectEnabledPlugins();
    Plugin installPlugin(Plugin plugin);
    int uninstallPlugin(Long pluginId);
    int enablePlugin(Long pluginId);
    int disablePlugin(Long pluginId);
    List<PluginConfig> getPluginConfig(Long pluginId);
    int setPluginConfig(PluginConfig config);
}
