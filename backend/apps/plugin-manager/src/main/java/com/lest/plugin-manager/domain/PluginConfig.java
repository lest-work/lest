package com.lest.plugin.manager.domain;

import java.io.Serializable;

/**
 * 插件配置对象 plugin_config
 *
 * @author yshan2028
 */
public class PluginConfig implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 配置ID */
    private Long configId;

    /** 插件ID */
    private Long pluginId;

    /** 配置Key */
    private String configKey;

    /** 配置值 */
    private String configValue;

    public Long getConfigId()
    {
        return configId;
    }

    public void setConfigId(Long configId)
    {
        this.configId = configId;
    }

    public Long getPluginId()
    {
        return pluginId;
    }

    public void setPluginId(Long pluginId)
    {
        this.pluginId = pluginId;
    }

    public String getConfigKey()
    {
        return configKey;
    }

    public void setConfigKey(String configKey)
    {
        this.configKey = configKey;
    }

    public String getConfigValue()
    {
        return configValue;
    }

    public void setConfigValue(String configValue)
    {
        this.configValue = configValue;
    }
}
