package com.lest.common.plugin.extension;

import java.io.Serializable;
import java.util.Map;

public class ExtensionDescriptor implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 扩展唯一标识 */
    private String id;

    /** 扩展名称 */
    private String name;

    /** 扩展点类型 */
    private String point;

    /** 所属插件 ID */
    private String pluginId;

    /** 扩展配置参数 */
    private Map<String, Object> config;

    /** 扩展实现类 */
    private String implementation;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPoint() { return point; }
    public void setPoint(String point) { this.point = point; }
    public String getPluginId() { return pluginId; }
    public void setPluginId(String pluginId) { this.pluginId = pluginId; }
    public Map<String, Object> getConfig() { return config; }
    public void setConfig(Map<String, Object> config) { this.config = config; }
    public String getImplementation() { return implementation; }
    public void setImplementation(String implementation) { this.implementation = implementation; }
}
