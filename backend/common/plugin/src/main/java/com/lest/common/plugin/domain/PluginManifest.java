package com.lest.common.plugin.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 插件清单（manifest.json 的运行时表示）
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PluginManifest implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String version;
    private String description;
    private String author;
    private String entry;
    private Map<String, String> config = new HashMap<>();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getEntry() { return entry; }
    public void setEntry(String entry) { this.entry = entry; }
    public Map<String, String> getConfig() { return config; }
    public void setConfig(Map<String, String> config) { this.config = config; }
}
