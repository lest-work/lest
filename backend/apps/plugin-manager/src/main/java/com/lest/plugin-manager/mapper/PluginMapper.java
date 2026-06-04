package com.lest.plugin.manager.mapper;

import com.lest.plugin.manager.domain.Plugin;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface PluginMapper
{
    List<Plugin> selectPluginList(Plugin plugin);
    Plugin selectPluginById(Long pluginId);
    Plugin selectPluginByKey(String pluginKey);
    List<Plugin> selectEnabledPlugins();
    int insertPlugin(Plugin plugin);
    int updatePlugin(Plugin plugin);
    int deletePluginById(Long pluginId);
}
