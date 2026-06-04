package com.lest.plugin.manager.service.impl;

import com.lest.plugin.manager.domain.Plugin;
import com.lest.plugin.manager.domain.PluginConfig;
import com.lest.plugin.manager.mapper.PluginConfigMapper;
import com.lest.plugin.manager.mapper.PluginMapper;
import com.lest.plugin.manager.service.IPluginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

@Service
public class PluginServiceImpl implements IPluginService
{
    @Autowired private PluginMapper pluginMapper;
    @Autowired private PluginConfigMapper configMapper;

    @Override
    public List<Plugin> selectPluginList(Plugin plugin)
    {
        return pluginMapper.selectPluginList(plugin);
    }

    @Override
    public Plugin selectPluginById(Long pluginId)
    {
        return pluginMapper.selectPluginById(pluginId);
    }

    @Override
    public List<Plugin> selectEnabledPlugins()
    {
        return pluginMapper.selectEnabledPlugins();
    }

    @Override
    public Plugin installPlugin(Plugin plugin)
    {
        Plugin existing = pluginMapper.selectPluginByKey(plugin.getPluginKey());
        if (existing != null)
        {
            throw new RuntimeException("Plugin with key '" + plugin.getPluginKey() + "' already installed");
        }
        plugin.setInstalledAt(new Date());
        plugin.setIsEnabled(0);
        pluginMapper.insertPlugin(plugin);
        return plugin;
    }

    @Override
    public int uninstallPlugin(Long pluginId)
    {
        configMapper.deleteByPluginId(pluginId);
        return pluginMapper.deletePluginById(pluginId);
    }

    @Override
    public int enablePlugin(Long pluginId)
    {
        Plugin plugin = new Plugin();
        plugin.setPluginId(pluginId);
        plugin.setIsEnabled(1);
        plugin.setEnabledAt(new Date());
        return pluginMapper.updatePlugin(plugin);
    }

    @Override
    public int disablePlugin(Long pluginId)
    {
        Plugin plugin = new Plugin();
        plugin.setPluginId(pluginId);
        plugin.setIsEnabled(0);
        return pluginMapper.updatePlugin(plugin);
    }

    @Override
    public List<PluginConfig> getPluginConfig(Long pluginId)
    {
        return configMapper.selectByPluginId(pluginId);
    }

    @Override
    public int setPluginConfig(PluginConfig config)
    {
        PluginConfig existing = configMapper.selectByPluginIdAndKey(config.getPluginId(), config.getConfigKey());
        if (existing != null)
        {
            existing.setConfigValue(config.getConfigValue());
            return configMapper.updateConfig(existing);
        }
        return configMapper.insertConfig(config);
    }
}
