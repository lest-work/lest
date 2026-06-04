package com.lest.common.plugin.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 插件描述符
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PluginDescriptor implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String pluginId;
    private String name;
    private String version;
    private String description;
    private String author;
    private String website;
    private String type;
    private Integer order;
    private Map<String, Object> config = new HashMap<>();
    private List<String> dependencies = new ArrayList<>();
    private List<String> hooks = new ArrayList<>();

    public String getPluginId() { return pluginId; }
    public void setPluginId(String pluginId) { this.pluginId = pluginId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Integer getOrder() { return order; }
    public void setOrder(Integer order) { this.order = order; }
    public Map<String, Object> getConfig() { return config; }
    public void setConfig(Map<String, Object> config) { this.config = config; }
    public List<String> getDependencies() { return dependencies; }
    public void setDependencies(List<String> dependencies) { this.dependencies = dependencies; }
    public List<String> getHooks() { return hooks; }
    public void setHooks(List<String> hooks) { this.hooks = hooks; }
}
