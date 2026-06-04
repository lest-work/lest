package com.lest.common.plugin.extension;

import java.io.Serializable;

public class ExtensionPointDescriptor implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 扩展点唯一标识 */
    private String name;

    /** 扩展点描述 */
    private String description;

    /** 所属插件 ID */
    private String pluginId;

    /** 扩展点类型 */
    private String type;

    /** 扩展点配置参数 */
    private Object config;

    public static Builder builder() { return new Builder(); }

    public static class Builder
    {
        private final ExtensionPointDescriptor instance = new ExtensionPointDescriptor();
        public Builder name(String name) { instance.name = name; return this; }
        public Builder description(String desc) { instance.description = desc; return this; }
        public Builder pluginId(String pluginId) { instance.pluginId = pluginId; return this; }
        public Builder type(String type) { instance.type = type; return this; }
        public Builder config(Object config) { instance.config = config; return this; }
        public ExtensionPointDescriptor build() { return instance; }
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getPluginId() { return pluginId; }
    public void setPluginId(String pluginId) { this.pluginId = pluginId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Object getConfig() { return config; }
    public void setConfig(Object config) { this.config = config; }
}
