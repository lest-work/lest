package com.lest.plugin.manager.mapper;

import com.lest.plugin.manager.domain.PluginConfig;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface PluginConfigMapper
{
    List<PluginConfig> selectByPluginId(Long pluginId);
    PluginConfig selectByPluginIdAndKey(Long pluginId, String configKey);
    int insertConfig(PluginConfig config);
    int updateConfig(PluginConfig config);
    int deleteByPluginId(Long pluginId);
}
